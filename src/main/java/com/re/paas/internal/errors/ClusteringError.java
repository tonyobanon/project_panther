package com.re.paas.internal.errors;

import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.errors.Error;

@BlockerBlockerTodo("Update from() method")
public enum ClusteringError implements Error {
	
	CLUSTER_NODE_ALREADY_RUNNING_ON_MACHINE(20, "Start failed. A cluster node is already running on this machine."),
	COULD_NOT_READ_APPLICATION_CODE_LOCATION(30, "Error occured while reading code location for this application: " + "{ref1}"),
	OS_NOT_SUPPORTED_BY_THIS_PLATFORM(40, "The operating system (" + "{ref1}" + ") is not yet supported by this platform"),
	
	EMPTY_AZURE_VMID_WAS_RETRIEVED(41, "An empty Azure VM UUID was retrieved"),
	INVALID_AZURE_VMID_WAS_RETRIEVED(42, "An Invalid Azure VM UUID (" + "{ref1}" + ") was retrieved"),
	
	COULD_NOT_REACH_NETWORK_ADDRESS(71, "Error occured while attempting to communicate the network address @" + "{ref1}"),
		
	ERROR_OCCURED_ON_SOCKET(73, "Error occured on socket --> localAddress (" + "{ref1}" + "), remoteAddress(" + "{ref2}" + ")"),
	ERROR_OCCURED_WHILE_STARTING_SERVER_SOCKET(74, "Error occured while starting server socket on (" + "{ref1}" + ": " + "{ref2}" + ")"),
	ERROR_OCCURED_WHILE_STARTING_CLIENT_SOCKET(75, "Error occured while starting client socket to (" + "{ref1}" + ": " + "{ref2}" + ")"),

	
	SOCKET_IO_ERROR_OCCURED_WHILE_GETTING_REQ_PARAMS(77, "Odyssey encountered a Socket IO error while reading request parameters. Source: " + "{ref1}"
			+ " , Destination: " + "{ref2}"),
	
	SOCKET_IO_ERROR_OCCURED_ON_WRITE(150, "An IO error occurred while writing to a network socket at remote address: " + "{ref1}"),
	
	REGION_HAS_INCORRECT_FORMAT(79, "Region: {ref1} has an incorrect format"),
	INSTANCE_TYPE_HAS_INCORRECT_FORMAT(80, "Instance Type: {ref1} has an incorrect format"),
	
	MASTER_DOES_NOT_HAVE_ROUTEABLE_ADDRESS(90, "The master instance with address: " + "{ref1}" + " does not have an internet routable ip address"),
	MASTER_NOT_IN_VIRTUAL_PRIVATE_NETWORK(91, "The master instance with address: " + "{ref1}" + " does not belong to any virtual network"),
	SUBNET_NOT_FOUND_ON_NETWORK(92, "The subnet with id: " + "{ref1}" + " was not found on the virtual network with id: " + "{ref2}"),
	ERROR_OCCURED_WHILE_MAKING_SERVICE_CALL(93, "An error occured while making a service call to " + "{ref1}" + ". Reason: " + "{ref2}"),
	
	
	// AWS: This means that calling isMapPublicIpOnLaunch() on the subnet returned false
	NOT_MAPPING_PUBLIC_IPS_INSTANCES_IN_SUBNET(94, "The subnet: " + "{ref1}"
			+ " is not properly configured. Enable it to automatically assign public IPs to instances launched in it."),
	
	
	// AWS: This means that dns hostnames are disabled in the Vpc of the master
	DNS_HOSTNAMES_ARE_CURRENTLY_DISABLED(95, "The Vpc: " + "{ref1}"
			+ " is not properly configured. Dns Hostnames must be enabled (It is currently disabled)."),
	MASTER_DOES_NOT_HAVE_PUBLIC_DNS_NAME(96, "The master instance: " + "{ref1}" + " does not have a public dns name. This is required.");


	private boolean isFatal = true;
	private int code;
	private String message;

	private ClusteringError(Integer code, String message) {
		this(code, message, true);
	}

	private ClusteringError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
	public String namespace() {
		return "clustering";
	}

	public static ClusteringError from(int value) {

		switch (value) {

		case 5:
			return ClusteringError.CLUSTER_NODE_ALREADY_RUNNING_ON_MACHINE;

		default:
			return null;
		}
	}

	@Override
	public boolean isFatal() {
		return isFatal;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}



}
