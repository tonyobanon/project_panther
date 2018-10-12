package com.re.paas.internal.clustering.master;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.events.NodeLeaveEvent;
import com.re.paas.api.clustering.generic.GenericFunction;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.clustering.MasterNodeRole;
import com.re.paas.internal.clustering.objectmodels.NodeLeaveRequest;
import com.re.paas.internal.clustering.objectmodels.NodeLeaveResult;

public class ClusterLeaveFunction extends AbstractClusterFunction<NodeLeaveRequest, NodeLeaveResult> {

	@Override
	public Class<? extends NodeRole> role() {
		return MasterNodeRole.class;
	}

	@Override
	public Function id() {
		return MasterFunction.CLUSTER_LEAVE;
	}

	@Override
	public NodeLeaveResult delegate(NodeLeaveRequest request) {

		// validate credentials
		Logger.get().info("Received NodeLeaveRequest from " + request.getRemoteAddress());

		NodeRegistry registry = NodeRegistry.get();
		
		// Dispatch NodeLeaveEvent event to all current cluster nodes
		registry.getNodes().keySet().forEach(k -> {
			Client.get(k)
			.execute(GenericFunction.DISPATCH_EVENT,
					new NodeLeaveEvent().setNodeId(request.getNodeId()));			
		});

		NodeLeaveResult result = new NodeLeaveResult().setSuccess(true);
		return result;
	}

}
