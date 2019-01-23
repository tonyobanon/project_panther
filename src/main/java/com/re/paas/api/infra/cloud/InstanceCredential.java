package com.re.paas.api.infra.cloud;

public abstract class InstanceCredential {

	private final String instanceId;
	
	public InstanceCredential(String instanceId) {
		this.instanceId = instanceId;
	}

	public abstract Class<? extends CloudEnvironment> provider();
	
	public abstract String toString();
	
	public abstract InstanceCredential fromString(String instanceId, String stringVal);

	public String getInstanceId() {
		return instanceId;
	}
	
}
