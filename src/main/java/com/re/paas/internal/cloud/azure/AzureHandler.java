package com.re.paas.internal.cloud.azure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Pattern;

import com.microsoft.azure.CloudException;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachine;
import com.microsoft.azure.management.compute.VirtualMachine.DefinitionStages.WithCreate;
import com.microsoft.azure.management.compute.VirtualMachine.DefinitionStages.WithOS;
import com.microsoft.azure.management.compute.VirtualMachine.DefinitionStages.WithPublicIPAddress;
import com.microsoft.azure.management.network.Network;
import com.microsoft.azure.management.network.NetworkInterface;
import com.microsoft.azure.management.network.NicIPConfiguration;
import com.microsoft.azure.management.network.PublicIPAddress;
import com.microsoft.azure.management.network.Subnet;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.classes.InstanceProfile;
import com.re.paas.api.clustering.classes.NodeInstanceType;
import com.re.paas.api.clustering.classes.OsPlatform;
import com.re.paas.api.cryto.RSAKeyPair;
import com.re.paas.api.infra.cloud.AbstractProviderHandler;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.infra.cloud.InstanceCredential;
import com.re.paas.api.infra.cloud.Tags;
import com.re.paas.internal.classes.AppDirectory;
import com.re.paas.internal.cloud.KVPair;
import com.re.paas.internal.clustering.classes.Utils;
import com.re.paas.internal.crypto.impl.CryptoUtils;
import com.re.paas.internal.errors.ClusteringError;
import com.re.paas.internal.networking.IPAddresses;

public class AzureHandler extends AbstractProviderHandler {

	private static Pattern VMID_PATTERN = Pattern.compile("(\\p{Alnum}){8}-(\\p{Alnum}){4}-(\\p{Alnum}){4}-(\\p{Alnum}){4}-(\\p{Alnum}){12}");
	
	@Override
	public String getInstanceId() {

		String uuid = null;

		OsPlatform os = Utils.getPlatform();

		switch (os) {

		case LINUX:

			try {

				String[] shellArgs = { "sudo dmidecode | grep UUID" };
				Process shellProcess = new ProcessBuilder(shellArgs).start();
				shellProcess.waitFor();
				BufferedReader shellReader = new BufferedReader(new InputStreamReader(shellProcess.getInputStream()));
				uuid = shellReader.readLine().split(KVPair.separator().pattern())[1];

			} catch (IOException | InterruptedException e) {
				Exceptions.throwRuntime(e);
			}

			break;

		case WINDOWS:

			try {

				InputStream in = AppDirectory.getBaseClassloader()
						.getResourceAsStream("cloud_providers/azure/AzureVMIDReader.ps1");

				File psFile = File.createTempFile("AzureVMIDReader", ".ps1");
				FileOutputStream out = new FileOutputStream(psFile);

				Utils.copyTo(in, out);

				String[] powershellArgs = { "powershell.exe", "-Noninteractive", "-OutputFormat Text",
						"-File " + psFile.getAbsolutePath() };
				Process powershellProcess = new ProcessBuilder(powershellArgs).start();
				powershellProcess.waitFor();
				BufferedReader powershellReader = new BufferedReader(
						new InputStreamReader(powershellProcess.getInputStream()));
				uuid = powershellReader.readLine();

				break;

			} catch (IOException | InterruptedException e) {
				Exceptions.throwRuntime(e);
			}
		case MAC:
			break;
		case SOLARIS:
			break;
		default:
			break;

		}

		if (uuid == null) {
			return (String) Exceptions
					.throwRuntime(PlatformException.get(ClusteringError.EMPTY_AZURE_VMID_WAS_RETRIEVED));
		}

		// Validate UUID Pattern
		if (!VMID_PATTERN.matcher(uuid).matches()) {
			return (String) Exceptions
					.throwRuntime(PlatformException.get(ClusteringError.INVALID_AZURE_VMID_WAS_RETRIEVED, uuid));
		}

		return uuid.trim();
	}

	@Override
	public InstanceProfile getInstanceProfile() {

		InstanceProfile profile = null;
		NodeRegistry nodeRegistry = NodeRegistry.get();
		
		CloudEnvironment env = CloudEnvironment.get();
		
		try {

			// ** Validate Instance Type Spec
			String vmSizeType = env.getInstanceTags().get(AzureTags.AZURE_VIRTUAL_MACHINE_SIZE_TYPE);
			
			if ((!AzureHelper.isValidVMSizeType(vmSizeType)) && (!vmSizeType.equals(NodeInstanceType.INHERIT.name()))
					&& (!vmSizeType.equals(NodeInstanceType.DYNAMIC.name()))) {

				Exceptions.throwRuntime(
						PlatformException.get(ClusteringError.INSTANCE_TYPE_HAS_INCORRECT_FORMAT, vmSizeType));
			}

			Azure azure = AzureHelper.getAzure();

			VirtualMachine azureVM = azure.virtualMachines().getById(nodeRegistry.getCloudUniqueId());

			// ** Validate Region
			String region = env.getInstanceTags().get(Tags.REGION);
			
			if (region != null) {
				if (!AzureHelper.isValidRegion(region)) {
					Exceptions.throwRuntime(PlatformException.get(ClusteringError.REGION_HAS_INCORRECT_FORMAT, region));
				}
			} else {
				region = azureVM.regionName();
			}

			PublicIPAddress publicIp = azureVM.getPrimaryPublicIPAddress();

			// Validate public ip
			if (publicIp == null) {
				Exceptions.throwRuntime(PlatformException.get(ClusteringError.MASTER_DOES_NOT_HAVE_ROUTEABLE_ADDRESS,
						nodeRegistry.getWkaHost().getHostAddress()));

			}

			String publicDnsName = publicIp.leafDomainLabel();

			// Validate public dns name
			if (publicDnsName == null) {
				Exceptions.throwRuntime(PlatformException.get(ClusteringError.MASTER_DOES_NOT_HAVE_PUBLIC_DNS_NAME));
			}

			publicDnsName = publicDnsName + "." + azureVM.regionName() + "." + AzureHelper.dnsNameSuffix();

			NetworkInterface azureNIC = azureVM.getPrimaryNetworkInterface();
			NicIPConfiguration azureIpConfig = azureNIC.primaryIPConfiguration();
			Network network = azureIpConfig.getNetwork();

			// Validate that the primary network interface is connected to a
			// virtual network
			if (network == null) {
				Exceptions.throwRuntime(PlatformException.get(ClusteringError.MASTER_NOT_IN_VIRTUAL_PRIVATE_NETWORK,
						nodeRegistry.getWkaHost().getHostAddress()));
			}

			String vnetId = network.id();
			String securityGroup = azureNIC.networkSecurityGroupId();
			String masterSubnetId = azureIpConfig.inner().subnet().id();
			String masterSubnetCIDR = azureIpConfig.inner().subnet().addressPrefix();

			// ** Validate private subnet id
			String slaveSubnetId = env.getInstanceTags().get(Tags.PRIVATE_SUBNET_ID);

			String slaveSubnetCIDR = null;

			for (Subnet subnet : network.subnets().values()) {
				if (subnet.inner().id().equals(slaveSubnetId)) {
					slaveSubnetCIDR = subnet.inner().addressPrefix();
				}
			}

			if (slaveSubnetCIDR == null) {
				Exceptions.throwRuntime(PlatformException.get(ClusteringError.SUBNET_NOT_FOUND_ON_NETWORK,
						slaveSubnetId, network.id()));
			}

			// Build Instance Profile

			profile = new InstanceProfile();

			profile.withImage(azureVM.osUnmanagedDiskVhdUri()).withDefaultInstanceTypeSpec(azureVM.size().toString())
					.withInstanceTypeSpec(vmSizeType).withDefaultRegion(azureVM.regionName()).withRegion(region)
					.withVirtualNetworkId(vnetId).withMasterSubnetId(masterSubnetId)
					.withMasterSubnetCIDR(masterSubnetCIDR).withSlaveSubnetId(slaveSubnetId)
					.withSlaveSubnetCIDR(slaveSubnetCIDR).withMasterPublicIp(publicIp.ipAddress())
					.withOsPlatform(Utils.getPlatform()).withSecurityGroup(securityGroup)
					.withMasterPublicDNSName(publicDnsName);

		} catch (CloudException e) {
			if (e instanceof CloudException) {

				Exceptions.throwRuntime(PlatformException.get(ClusteringError.ERROR_OCCURED_WHILE_MAKING_SERVICE_CALL,
						AzureHelper.name(), e.getMessage()));
			} else {
				Exceptions.throwRuntime(e);
			}
		}

		return profile;
	}
	
	@Override
	public InstanceCredential startVM(Boolean master, Map<String, String> tags) {

		InstanceProfile iProfile = getInstanceProfile();
		
		// Generate clusterNodeId
		String clusterNodeId = Utils.newSecureRandom();

		// Note we must configure its networking components properly, since this
		// is critical for cluster authentication, later on

		String privateIp;
		Boolean associatePublicIp;
		String subnetId;
	

		if (master) {

			privateIp = IPAddresses.generateAddress(iProfile.getMasterSubnetCIDR()).getHostAddress();

			associatePublicIp = true;
			subnetId = iProfile.getMasterSubnetId();

		} else {

			// Randomly assign a private ip in the slave subnet.
			privateIp = IPAddresses.generateAddress(iProfile.getSlaveSubnetCIDR()).getHostAddress();

			associatePublicIp = true;
			subnetId = iProfile.getSlaveSubnetId();
		}

		String instanceId = null;

		// Generate credentials
		String username = Utils.newSecureRandom();
		String password = Utils.newSecureRandom();
		RSAKeyPair keypair = null;

		String resourceName = tags.get(Tags.RESOURCE_NAME_TAG);

		try {

			// In Azure, security-groups are applied subnet-level, it cannot
			// be applied to all new cluster instances created.

			Azure azure = AzureHelper.getAzure();

			// Just get Network model
			Network vnet = azure.networks().getById(iProfile.getVirtualNetworkId());

			WithPublicIPAddress withPublicIpAddress = azure.virtualMachines().define(resourceName)
					.withRegion(iProfile.getRegion()).withExistingResourceGroup(AzureHelper.getResourceGroup())
					.withExistingPrimaryNetwork(vnet).withSubnet(subnetId).withPrimaryPrivateIPAddressStatic(privateIp);

			WithOS withOS;

			// Branch -> associatePublicIp
			// Remember: Unlike AWS, azure, public IP assignment is
			// dependent on the instance rather than the subnet

			if (associatePublicIp) {
				// This is a master replica, so we assign a public Ip
				// Unlike AWS, we enter a dnsLabel that will resolve to the
				// dynamic public Ip
				withOS = withPublicIpAddress
						.withNewPrimaryPublicIPAddress(clusterNodeId + "-" + NodeRegistry.get().clusterName());
			} else {

				// By default, slave instances are launched without public
				// IPs
				withOS = withPublicIpAddress.withoutPrimaryPublicIPAddress();
			}

			WithCreate withCreate = null;

			// Branch -> osPlatform

			switch (iProfile.getOsPlatform()) {
			case LINUX:

				keypair = new RSAKeyPair();

				withCreate = withOS.withStoredLinuxImage(iProfile.getImage()).withRootUsername(username)
						.withRootPassword(password)
						.withSsh(CryptoUtils.asString("RSA", keypair.getPublicKey()))
						.withComputerName(resourceName);

				break;
			case WINDOWS:

				withCreate = withOS.withStoredWindowsImage(iProfile.getImage()).withAdminUsername(username)
						.withAdminPassword(password).withComputerName(resourceName);

				break;
			case MAC:
				break;
			case SOLARIS:
				break;
			default:
				break;
			}
			
			WithCreate vmCreate = withCreate.withSize(iProfile.getInstanceTypeSpec());
			
			tags.forEach((k, v) -> {
				vmCreate.withTag(k, v);
			});
			
			VirtualMachine vm = vmCreate.create();

			instanceId = vm.vmId();

			// Save Credentials to Registry
			AzureVMCredential credential = new AzureVMCredential(instanceId, username, password).withKeyPair(keypair);
			return credential;

		} catch (CloudException e) {
			
			Exceptions.throwRuntime(PlatformException.get(ClusteringError.ERROR_OCCURED_WHILE_MAKING_SERVICE_CALL,
					AzureHelper.name(), e.getMessage()));
		
		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}
		
		return null;
	}
	
	@Override
	public void stopVM(String instanceId) {
		// TODO Auto-generated method stub
		
	}

}
