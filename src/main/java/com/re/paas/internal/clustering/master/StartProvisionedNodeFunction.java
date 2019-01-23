package com.re.paas.internal.clustering.master;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.clustering.master.NodeProvisioningRequest;
import com.re.paas.api.clustering.master.NodeProvisioningResult;
import com.re.paas.internal.clustering.MasterNodeRole;

public class StartProvisionedNodeFunction extends AbstractClusterFunction<NodeProvisioningRequest, NodeProvisioningResult> {

	@Override
	public Class<? extends NodeRole> role() {
		return MasterNodeRole.class;
	}

	@Override
	public Function id() {
		return MasterFunction.START_PROVISIONED_NODE;
	}

	@Override
	public NodeProvisioningResult delegate(NodeProvisioningRequest request) {
		return NodeRole.getDelegate().getMasterRole().startProvisionedNode(request);
	}

}
