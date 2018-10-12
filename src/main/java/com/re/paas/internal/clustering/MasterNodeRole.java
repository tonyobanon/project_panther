package com.re.paas.internal.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.clustering.AbstractMasterNodeRole;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.clustering.classes.InstanceProfile;
import com.re.paas.api.clustering.classes.NodeState;
import com.re.paas.api.clustering.events.NodeJoinEvent;
import com.re.paas.api.clustering.events.NodeLeaveEvent;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;

public class MasterNodeRole extends AbstractMasterNodeRole {

	private static short lastAssignedNodeId = -1;

	private static ClusterCredentials ClusterCredentials;
	protected static InstanceProfile iProfile;
	public static final Map<Short, String> instanceIdMap = Maps.newHashMap();

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

		if (registry.isAutoScalingEnabled()) {

			/*
			 * Set up Cloud Providers It should be noted that any invalid credentials
			 * (including resource group, subscription, e.t.c) provided, may not throw any
			 * exception until at a later time, after all clustering services have been
			 * started
			 * 
			 */

			// Fetch Instance Profile
			iProfile = CloudEnvironment.get().autoScaleDelegate().getInstanceProfile();
		}

		// Assign nodeId
		registry.setNodeId(nextNodeId());

		// Generate cluster credentials
		ClusterCredentials = new ClusterCredentials(Utils.newShortRandom(), Utils.newShortRandom());

		BaseNodeSpec spec = new BaseNodeSpec().setId(registry.getNodeId()).setName(registry.getName())
				.setRemoteAddress(registry.getClusteringAddress()).setInboundPort(registry.getInboundPort())
				.setState(NodeState.ONLINE).setRoles(NodeRole.get().values()).setJoinDate(Dates.now());

		// Add itself to the list of nodes
		new NodeEventListener().onNodeJoin(new NodeJoinEvent().setSpec(Lists.newArrayList(spec)));

		// Notify NodeRole Delegate
		AbstractEventDelegate.getInstance().dispatch(new RoleStartCompleteEvent(true));
	}

	@Override
	public void stop() {

	}

	@Override
	public ClusterCredentials getClusterCredentials() {
		return ClusterCredentials;
	}

	@Override
	public Map<Short, String> getAutoProvisionedNodes() {
		return instanceIdMap;
	}

	@Override
	public Short nextNodeId() {

		if (!unassignedNodeIds.isEmpty()) {
			return unassignedNodeIds.remove(unassignedNodeIds.size() - 1);
		}

		lastAssignedNodeId++;
		return lastAssignedNodeId;
	}

	@Override
	public void onNodeLeave(NodeLeaveEvent evt) {

		Short nodeId = evt.getNodeId();

		// Add this nodeId to the unassigned pool.
		unassignedNodeIds.add(nodeId);

		// Shutdown VM
		CloudEnvironment.get().autoScaleDelegate().stopVM(instanceIdMap.get(nodeId));
	}

	public static String getNextResourceName() {
		return Platform.getPlatformPrefix()+ "-" + NodeRegistry.get().getNodes().size() + 1;
	}

}
