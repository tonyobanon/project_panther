package com.re.paas.internal.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.clustering.AbstractMasterNodeRole;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.clustering.classes.NodeState;
import com.re.paas.api.clustering.events.NodeJoinEvent;
import com.re.paas.api.clustering.events.NodeLeaveEvent;
import com.re.paas.api.clustering.master.NodeProvisioningRequest;
import com.re.paas.api.clustering.master.NodeProvisioningResult;
import com.re.paas.api.clustering.master.NodeReleaseRequest;
import com.re.paas.api.clustering.master.NodeReleaseResult;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.infra.cloud.InstanceCredential;
import com.re.paas.api.infra.cloud.Tags;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.networking.AddressResolver;
import com.re.paas.api.utils.Dates;
import com.re.paas.internal.Platform;

public class MasterNodeRole extends AbstractMasterNodeRole {

	private static final Logger LOG = Logger.get(MasterNodeRole.class);

	private static short lastAssignedNodeId = -1;

	private static final Map<Short, InstanceCredential> instanceCredentials = Maps.newHashMap();
	private static final Map<String, InstanceCredential> tempInstanceCredentials = Maps.newHashMap();

	private static final List<Short> unassignedNodeIds = new ArrayList<Short>();

	@Override
	public String name() {
		return "cluster-master";
	}

	@Override
	public List<Class<? extends NodeRole>> dependencies() {
		return new FluentArrayList<Class<? extends NodeRole>>();
	}

	@Override
	public boolean applies() {
		CloudEnvironment env = CloudEnvironment.get();
		return env.clusteringHost().equals(env.wkaHost()) && hasMasterTrait();
	}

	@Override
	public void start() {

		NodeRegistry registry = NodeRegistry.get();


			/*
			 * Set up Cloud Providers It should be noted that any invalid credentials
			 * (including resource group, subscription, e.t.c) provided, may not throw any
			 * exception until at a later time, after all clustering services have been
			 * started
			 * 
			 */

		// Assign nodeId
		registry.setNodeId(nextNodeId());

		BaseNodeSpec spec = new BaseNodeSpec().setId(registry.getNodeId()).setName(registry.getName())
				.setRemoteAddress(registry.getClusteringAddress()).setInboundPort(registry.getInboundPort())
				.setState(NodeState.ONLINE).setRoles(NodeRole.get().values()).setJoinDate(Dates.now());

		// Add itself to the list of nodes
		new NodeEventListener().onNodeJoin(new NodeJoinEvent().withNodes(Lists.newArrayList(spec)));

		// Notify NodeRole Delegate
		AbstractEventDelegate.getInstance().dispatch(new RoleStartCompleteEvent(true), false);
	}

	@Override
	public void stop() {

	}

	@Override
	public Map<Short, String> getAutoProvisionedNodes() {

		Map<Short, String> result = new HashMap<>();

		instanceCredentials.forEach((k, v) -> {
			result.put(k, v.getInstanceId());
		});

		return result;
	}
	
	public Short nextNodeId() {

		if (!unassignedNodeIds.isEmpty()) {
			return unassignedNodeIds.remove(unassignedNodeIds.size() - 1);
		}

		lastAssignedNodeId++;
		return lastAssignedNodeId;
	}

	@BlockerTodo
	@Override
	public NodeProvisioningResult startProvisionedNode(NodeProvisioningRequest request) {
		
		LOG.info("Node: " + request.getNodeId() + " requested a new cluster node, reason = " + request.getReason().toString());
		
		CloudEnvironment env = CloudEnvironment.get();

		Map<String, String> tags = new HashMap<>();

		String rName = getNextResourceName();

		tags.put(Tags.RESOURCE_NAME_TAG, rName);
		tags.put(Tags.MASTER_TAG, request.getMaster().toString());
		tags.put(Tags.WKA_HOST_TAG, AddressResolver.get().clusteringHost().getHostAddress());

		// Add cluster credentials
		ClusterCredentials clusterCredentials = env.credentials();

		tags.put(Tags.CLUSTER_ACCESS_KEY_TAG, clusterCredentials.getAccessKey());
		tags.put(Tags.CLUSTER_SECRET_KEY_TAG, clusterCredentials.getSecretKey());

		NodeProvisioningResult result = new NodeProvisioningResult();

		try {

			LOG.info("Attempting to provision new cluster node with resource name: " + rName);

			InstanceCredential credentials = env.providerDelegate().startVM(request.getMaster(), tags);
			tempInstanceCredentials.put(credentials.getInstanceId(), credentials);

			LOG.info("Cluster node (" + rName + ") successfully created with instance id: "
					+ credentials.getInstanceId());

		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}
		
		return result;
	}

	@Override
	public NodeReleaseResult releaseProvisionedNode(NodeReleaseRequest request) {

		String instanceId = instanceCredentials.get(request.getNodeId()).getInstanceId();
		NodeReleaseResult result = new NodeReleaseResult();

		try {
			CloudEnvironment.get().providerDelegate().stopVM(instanceId);
		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}

		return result;
	}
	
	@Override
	public void onNodeJoin(NodeJoinEvent evt) {
		
		evt.getNodes().forEach(spec -> {
			
			// Now that this node has been assigned an id, remove from tempInstanceCredentials
			// and save to instanceCredential
			
			spec.getId();
			
		});
	}
	

	@Override
	public void onNodeLeave(NodeLeaveEvent evt) {

		Short nodeId = evt.getNodeId();

		// Add this nodeId to the unassigned pool.
		unassignedNodeIds.add(nodeId);

		// Shutdown VM
		CloudEnvironment.get().providerDelegate().stopVM(instanceCredentials.get(nodeId).getInstanceId());
	}

	private static String getNextResourceName() {
		return Platform.getPlatformPrefix() + "-" + NodeRegistry.get().getNodes().size() + 1;
	}

}
