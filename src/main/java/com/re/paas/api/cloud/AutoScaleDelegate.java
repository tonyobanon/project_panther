package com.re.paas.api.cloud;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.clustering.classes.InstanceProfile;

public abstract class AutoScaleDelegate {

	/**
	 * This method registers the credentials for auto-scaled nodes in the cluster
	 * @param credential
	 */
	@BlockerTodo
	public void addInstanceCredential(InstanceCredential credential) {
	}
	
	/**
	 * This method unregisters the credentials for auto-scaled nodes in the cluster
	 * @param credential
	 */
	@BlockerTodo
	public void removeInstanceCredential(Class<? extends CloudEnvironment> provider, String instanceId) {
	}
	
	public abstract String getInstanceId();

	public abstract InstanceProfile getInstanceProfile();

	public abstract void startVM(InstanceProfile iProfile, ClusterCredentials auth, Boolean master);

	public abstract void stopVM(String instanceId);
}
