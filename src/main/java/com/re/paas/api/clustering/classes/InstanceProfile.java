package com.re.paas.api.clustering.classes;

import java.util.ArrayList;
import java.util.List;

public class InstanceProfile {

	private String image;

	private OsPlatform osPlatform;

	private String defaultInstanceTypeSpec;

	private String instanceTypeSpec;

	private String defaultRegion;

	private String region;

	private String virtualNetworkId;

	private List<String> securityGroups;

	private String masterSubnetId;

	private String masterSubnetCIDR;

	private String slaveSubnetId;

	private String slaveSubnetCIDR;

	private String masterPublicIp;
	
	private String masterPublicDNSName;

	public String getVirtualNetworkId() {
		return virtualNetworkId;
	}

	public InstanceProfile withVirtualNetworkId(String id) {
		this.virtualNetworkId = id;
		return this;
	}

	public String getSlaveSubnetId() {
		return slaveSubnetId;
	}

	public InstanceProfile withSlaveSubnetId(String subnetId) {
		this.slaveSubnetId = subnetId;
		return this;
	}

	public String getSlaveSubnetCIDR() {
		return slaveSubnetCIDR;
	}

	public InstanceProfile withSlaveSubnetCIDR(String slaveSubnetCIDR) {
		this.slaveSubnetCIDR = slaveSubnetCIDR;
		return this;
	}

	public String getMasterPublicIp() {
		return masterPublicIp;
	}

	public InstanceProfile withMasterPublicIp(String ip) {
		this.masterPublicIp = ip;
		return this;
	}

	public String getRegion() {
		return region;
	}

	public InstanceProfile withRegion(String region) {
		this.region = region;
		return this;
	}

	public String getImage() {
		return image;
	}

	public InstanceProfile withImage(String image) {
		this.image = image;
		return this;
	}

	public String getInstanceTypeSpec() {
		return instanceTypeSpec;
	}

	public InstanceProfile withInstanceTypeSpec(String instanceTypeSpec) {
		this.instanceTypeSpec = instanceTypeSpec;
		return this;
	}

	public String getDefaultInstanceTypeSpec() {
		return defaultInstanceTypeSpec;
	}

	public InstanceProfile withDefaultInstanceTypeSpec(String defaultInstanceTypeSpec) {
		this.defaultInstanceTypeSpec = defaultInstanceTypeSpec;
		return this;
	}

	public String getMasterSubnetId() {
		return masterSubnetId;
	}

	public InstanceProfile withMasterSubnetId(String defaultSubnetId) {
		this.masterSubnetId = defaultSubnetId;
		return this;
	}

	public String getMasterSubnetCIDR() {
		return masterSubnetCIDR;
	}

	public InstanceProfile withMasterSubnetCIDR(String masterSubnetCIDR) {
		this.masterSubnetCIDR = masterSubnetCIDR;
		return this;
	}

	public String getDefaultRegion() {
		return defaultRegion;
	}

	public InstanceProfile withDefaultRegion(String defaultRegion) {
		this.defaultRegion = defaultRegion;
		return this;
	}

	public OsPlatform getOsPlatform() {
		return osPlatform;
	}

	public InstanceProfile withOsPlatform(OsPlatform osPltform) {
		this.osPlatform = osPltform;
		return this;
	}

	public List<String> getSecurityGroup() {
		return securityGroups;
	}

	public InstanceProfile withSecurityGroup(List<String> securityGroups) {
		this.securityGroups = securityGroups;
		return this;
	}

	public InstanceProfile withSecurityGroup(String securityGroup) {
		this.securityGroups = new ArrayList<>(1);
		this.securityGroups.add(securityGroup);
		return this;
	}

	public String getMasterPublicDNSName() {
		return masterPublicDNSName;
	}

	public InstanceProfile withMasterPublicDNSName(String masterPublicDNSName) {
		this.masterPublicDNSName = masterPublicDNSName;
		return this;
	}

}
