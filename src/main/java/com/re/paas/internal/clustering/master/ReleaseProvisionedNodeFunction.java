package com.re.paas.internal.clustering.master;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.clustering.master.NodeReleaseRequest;
import com.re.paas.api.clustering.master.NodeReleaseResult;
import com.re.paas.internal.clustering.MasterNodeRole;

public class ReleaseProvisionedNodeFunction extends AbstractClusterFunction<NodeReleaseRequest, NodeReleaseResult> {

	@Override
	public Class<? extends NodeRole> role() {
		return MasterNodeRole.class;
	}

	@Override
	public Function id() {
		return MasterFunction.RELEASE_PROVISIONED_NODE;
	}

	@Override
	public NodeReleaseResult delegate(NodeReleaseRequest request) {
		return NodeRole.getDelegate().getMasterRole().releaseProvisionedNode(request);
	}

}
