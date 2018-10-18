package com.re.paas.internal.clustering.master;

import java.util.List;

import com.google.common.collect.Lists;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.AbstractMasterNodeRole;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.clustering.classes.NodeState;
import com.re.paas.api.clustering.events.NodeJoinEvent;
import com.re.paas.api.clustering.events.NodeStateChangeEvent;
import com.re.paas.api.clustering.generic.GenericFunction;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.Dates;
import com.re.paas.internal.clustering.MasterNodeRole;
import com.re.paas.internal.clustering.NodeRoleHelper;
import com.re.paas.internal.clustering.objectmodels.NodeJoinRequest;
import com.re.paas.internal.clustering.objectmodels.NodeJoinResult;
import com.re.paas.internal.filesystems.FileSystemChangeEvent;

public class ClusterJoinFunction extends AbstractClusterFunction<NodeJoinRequest, NodeJoinResult> {

	@Override
	public Class<? extends NodeRole> role() {
		return MasterNodeRole.class;
	}

	@Override
	public Function id() {
		return MasterFunction.CLUSTER_JOIN;
	}

	@Override
	public NodeJoinResult delegate(NodeJoinRequest request) {

		// validate credentials
		Logger.get().info("Received NodeJoinRequest from " + request.getInboundAddress());

		ClusterCredentials credentials = CloudEnvironment.get().credentials();

		if (!(request.getCredentials().getAccessKey().equals(credentials.getAccessKey())
				&& request.getCredentials().getSecretKey().equals(credentials.getSecretKey()))) {

			Logger.get().info("Authentication failed for node: " + request.getInboundAddress());
			return new NodeJoinResult().setSuccess(false).setMessage("Authentication failed");
		}

		
		Logger.get().info("Authentication succeeded for node: " + request.getInboundAddress());

		
		NodeRegistry registry = NodeRegistry.get();
		AbstractMasterNodeRole master = NodeRole.getDelegate().getMasterRole();

		Short nodeId = master.nextNodeId();

		final BaseNodeSpec newNode = new BaseNodeSpec().setId(nodeId).setName(request.getNodeName())
				.setRemoteAddress(request.getInboundAddress())
				.setInboundPort(request.getInboundPort()).setState(NodeState.STARTING)
				.setRoles(NodeRoleHelper.fromString(request.getRoles())).setJoinDate(Dates.now());

		
		
		// Dispatch NodeJoinEvent to newNode.getRemoteAddress(), for it to register
		// current cluster nodes

		Client.get(newNode.getRemoteAddress(), newNode.getInboundPort()).execute(GenericFunction.ASYNC_DISPATCH_EVENT,
				new NodeJoinEvent().setSpec(Lists.newArrayList(registry.getNodes().values())));

		
		// Dispatch FileSystemChangeEvent to newNode.getRemoteAddress(), for it to register
		// the user's preferred file system provider

		Client.get(newNode.getRemoteAddress(), newNode.getInboundPort()).execute(GenericFunction.ASYNC_DISPATCH_EVENT,
				new FileSystemChangeEvent());

		
		
		List<BaseNodeSpec> nodes = Lists.newArrayList(registry.getNodes().values());
		nodes.add(newNode);

		// Dispatch NodeJoinEvent event to all current cluster nodes, for them to
		// register newNode
		nodes.forEach((v) -> {
			Client.get(v.getId()).execute(GenericFunction.DISPATCH_EVENT,
					new NodeJoinEvent().setSpec(FluentArrayList.asList(newNode)));

		});

		// Dispatch NodeStateChangeEvent event to all current cluster nodes, for them to
		// update the status of newNode
		nodes.forEach((v) -> {
			Client.get(v.getId()).execute(GenericFunction.ASYNC_DISPATCH_EVENT,
					new NodeStateChangeEvent().setNodeId(newNode.getId()).setNewState(NodeState.ONLINE));

		});

		NodeJoinResult result = new NodeJoinResult().setSuccess(true).setNodeId(nodeId)
				.setMasterNodeId(registry.getNodeId());
		return result;
	}

}
