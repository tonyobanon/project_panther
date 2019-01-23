package com.re.paas.api.infra.cloud;

import java.util.Map;

import com.re.paas.api.clustering.classes.InstanceProfile;

public abstract class AbstractProviderHandler {
	
	public abstract String getInstanceId();

	public abstract InstanceProfile getInstanceProfile();

	public abstract InstanceCredential startVM(Boolean master, Map<String, String> tags);

	public abstract void stopVM(String instanceId);
}
