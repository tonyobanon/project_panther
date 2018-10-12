package com.re.paas.internal.clustering;

import java.util.List;

import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.events.NodeLeaveEvent;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.clustering.objectmodels.NodeJoinRequest;
import com.re.paas.internal.clustering.objectmodels.NodeJoinResult;

public class SlaveNodeRole extends NodeRole {

	@Override
	public String name() {
		return "cluster-slave";
	}

	@Override
	public List<Class<? extends NodeRole>> dependencies() {
		return new FluentArrayList<Class<? extends NodeRole>>();
	}

	@Override
	public boolean applies() {
		CloudEnvironment env = CloudEnvironment.get();
		return (!env.clusteringHost().equals(env.wkaHost())) && (!hasMasterTrait());
	}

	@Override
	public void start() {

		NodeRegistry registry = NodeRegistry.get();

		Logger.get().info("Requesting to join cluster: " + registry.clusterName());

		Client.get()
				.execute(MasterFunction.CLUSTER_JOIN, NodeJoinRequest.get(), NodeJoinResult.class).thenAccept((r) -> {

					if (r.isSuccess()) {

						Logger.get().info(
								"Node: " + registry.getClusteringAddress() + " joined the cluster successfully");
						
						registry.setNodeId(r.getNodeId());
						registry.setMasterNodeId(r.getMasterNodeId());

					} else {
						Logger.get().info("Node: " + registry.getClusteringAddress()
								+ " could not join cluster because " + r.getMessage());
					}

					// Notify NodeRole Delegate
					AbstractEventDelegate.getInstance().dispatch(new RoleStartCompleteEvent(r.isSuccess()));
				});
	}

	@Override
	public void stop() {
	}
	
	@Override
	public void onNodeLeave(NodeLeaveEvent evt) {
	}

}
