package com.re.paas.internal.boostrapping.clustering.master;

import static com.re.paas.api.clustering.generic.GenericFunction.ASYNC_DISPATCH_EVENT;
import static com.re.paas.api.clustering.generic.GenericFunction.DISPATCH_EVENT;
import static com.re.paas.api.clustering.slave.SlaveFunction.INGEST_ADAPTER_CONFIG;

import java.util.Map;
import java.util.Map.Entry;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AbstractAdapterDelegate;
import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.AbstractMasterNodeRole;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.clustering.classes.ClusterDestination;
import com.re.paas.api.clustering.classes.ClusteringUtils;
import com.re.paas.api.clustering.classes.NodeState;
import com.re.paas.api.clustering.events.NodeJoinEvent;
import com.re.paas.api.clustering.events.NodeStateChangeEvent;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.Dates;
import com.re.paas.internal.clustering.MasterNodeRole;
import com.re.paas.internal.clustering.NodeRoleHelper;
import com.re.paas.internal.clustering.objectmodels.IngestAdapterConfigRequest;
import com.re.paas.internal.clustering.objectmodels.IngestAdapterConfigResponse;
import com.re.paas.internal.clustering.objectmodels.NodeJoinRequest;
import com.re.paas.internal.clustering.objectmodels.NodeJoinResult;
import com.re.paas.internal.utils.Maps;

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
				.setRemoteAddress(request.getInboundAddress()).setInboundPort(request.getInboundPort())
				.setState(NodeState.STARTING).setRoles(NodeRoleHelper.fromString(request.getRoles()))
				.setJoinDate(Dates.now());

		// Dispatch NodeJoinEvent to newNode.getRemoteAddress(), for it to register
		// current cluster nodes

		Map<String, Boolean> r1 = Function.execute(ClusterDestination.spec(newNode), DISPATCH_EVENT,
				new NodeJoinEvent().withNodes(registry.getNodes().values()).withNode(newNode), Boolean.class);

		if (!Maps.first(r1).equals(Boolean.TRUE)) {
			return new NodeJoinResult().setSuccess(false).setMessage(
					"Error occurred while registering cluster nodes on new node: " + newNode.getRemoteAddress());
		}

		// Dispatch NodeJoinEvent event to all current cluster nodes, for them to
		// register newNode.

		Map<String, Boolean> r2 = Function.execute(ClusterDestination.ALL_NODES, DISPATCH_EVENT,
				new NodeJoinEvent().withNode(newNode), Boolean.class);

		for (Entry<?, ?> e : r2.entrySet()) {
			if (!e.getValue().equals(Boolean.TRUE)) {
				return new NodeJoinResult().setSuccess(false)
						.setMessage("Error occurred while registering new node on existing cluster node : ("
								+ e.getKey() + ", " + ClusteringUtils.getNodeId(e.getKey().toString()) + ")");
			}
		}

		// Transfer adapter configurations

		IngestAdapterConfigRequest ingestRequest = new IngestAdapterConfigRequest();

		for (AdapterType type : AdapterType.values()) {
			AbstractAdapterDelegate<? extends Adapter> delegate = Singleton.get(type.getDelegateType());
			AdapterConfig config = delegate.getConfig();
			ingestRequest.addAdapterConfig(config);
		}

		IngestAdapterConfigResponse r3 = Function.execute(newNode, INGEST_ADAPTER_CONFIG, ingestRequest,
				IngestAdapterConfigResponse.class);

		for (Entry<AdapterType, Object> e : r3.getStatus().entrySet()) {

			AdapterType type = e.getKey();
			Object status = e.getValue();

			if (!status.equals(Boolean.TRUE)) {

				StringBuilder errMessage = new StringBuilder();

				errMessage.append("Error occured while configuring " + type.toString() + " adapter, ")
						.append("type = " + type)
						.append("msg = '" + (status instanceof Throwable ? ((Throwable) status).getLocalizedMessage()
								: status.toString()) + "'");

				return new NodeJoinResult().setSuccess(false).setMessage(errMessage.toString());
			}
		}

		// Transfer applications (note: delegate should contain a pre remove function to
		// indicate that a resource set want to taken down)

		// Set platform (slave) as installed

		// Perform Consolidation(s)

		// Dispatch NodeStateChangeEvent event to all current cluster nodes, for them to
		// update the status of newNode.

		Function.execute(ClusterDestination.ALL_NODES, ASYNC_DISPATCH_EVENT,
				new NodeStateChangeEvent().setNodeId(newNode.getId()).setNewState(NodeState.ONLINE));

		NodeJoinResult result = new NodeJoinResult().setSuccess(true).setNodeId(nodeId)
				.setMasterNodeId(registry.getNodeId());
		return result;
	}

}
