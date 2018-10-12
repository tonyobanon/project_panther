package com.re.paas.api.clustering;

import java.util.Map;

import com.re.paas.api.clustering.classes.ClusterCredentials;

public abstract class AbstractMasterNodeRole extends NodeRole {

	public abstract Short nextNodeId();
	
	public abstract Map<Short, String> getAutoProvisionedNodes();
	
	public abstract ClusterCredentials getClusterCredentials();
}
