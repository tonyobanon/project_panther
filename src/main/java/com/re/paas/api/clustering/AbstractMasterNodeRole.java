package com.re.paas.api.clustering;

import java.util.Map;

import com.re.paas.api.clustering.master.NodeProvisioningRequest;
import com.re.paas.api.clustering.master.NodeProvisioningResult;
import com.re.paas.api.clustering.master.NodeReleaseRequest;
import com.re.paas.api.clustering.master.NodeReleaseResult;

public abstract class AbstractMasterNodeRole extends NodeRole {
	
	public abstract Map<Short, String> getAutoProvisionedNodes();
	
	public abstract Short nextNodeId();
	
	public abstract NodeProvisioningResult startProvisionedNode(NodeProvisioningRequest request);
	
	public abstract NodeReleaseResult releaseProvisionedNode(NodeReleaseRequest request);

}
