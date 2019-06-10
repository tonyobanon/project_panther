package com.re.paas.internal.clustering;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.internal.Platform;
import com.re.paas.internal.cloud.CloudEnvironmentAdapter;
import com.re.paas.internal.clustering.classes.ServerStartEvent;
import com.re.paas.internal.clustering.objectmodels.NodeLeaveRequest;
import com.re.paas.internal.clustering.objectmodels.NodeLeaveResult;
import com.re.paas.internal.compute.Scheduler;

public class DefaultNodeRegistry implements NodeRegistry {

	private static final Logger LOG = Logger.get(DefaultNodeRegistry.class);
	
	private static Short nodeId;

	private static Short masterNodeId;

	private static String name;

	private static String clusterName;

	private static Integer inboundPort;

	private static String cloudUniqueId;

	private static InetAddress clusteringAddress;

	private static InetAddress wkaHost;
	private static Integer wkaInboundPort;

	private static Server server;

	private static Map<Short, BaseNodeSpec> nodes = Maps.newHashMap();
	
	private static Boolean logClusterNodesAtIntervals = false;

	@Override
	@MethodMeta
	public void setNodeId(Short nodeId) {
		DefaultNodeRegistry.nodeId = nodeId;
	}

	@Override
	public Short getNodeId() {
		return nodeId;
	}

	@Override
	@MethodMeta
	public void setMasterNodeId(Short nodeId) {
		masterNodeId = nodeId;
	}

	@Override
	public Short getMasterNodeId() {
		return masterNodeId;
	}

	@Override
	public String clusterName() {
		return clusterName;
	}

	@Override
	public Integer getInboundPort() {
		return inboundPort;
	}

	@Override
	public String getCloudUniqueId() {
		return cloudUniqueId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public InetAddress getClusteringAddress() {
		return clusteringAddress;
	}

	@Override
	public InetAddress getWkaHost() {
		return wkaHost;
	}

	@Override
	public Integer getWkaInboundPort() {
		return wkaInboundPort;
	}

	@Override
	public Server getServer() {
		return server;
	}

	@Override
	public CompletableFuture<Void> start() {

		if (Files.exists(Paths.get(getTempFile()))) {
			// Exceptions.throwRuntime(PlatformException.get(ClusteringError.CLUSTER_NODE_ALREADY_RUNNING_ON_MACHINE));
		}

		// Create temporary file
		try {
			File temp = new File(getTempFile());
			temp.deleteOnExit();
			temp.createNewFile();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		CloudEnvironment env = CloudEnvironment.get();

		// Set Cluster Name
		clusterName = env.clusterName();


		LOG.debug("Starting node on cluster: " + clusterName);

		// Set node name
		DefaultNodeRegistry.name = CloudEnvironmentAdapter.getResourceName();

		// Get clustering address
		Logger.get().info("Acquiring clustering address");
		clusteringAddress = env.clusteringHost();
		LOG.debug("Using clustering address: " + CloudEnvironment.get().clusteringHost().getHostAddress());

		// Set Inbound port
		DefaultNodeRegistry.inboundPort = env.clusteringPort();
		LOG.debug("Using port: " + inboundPort);

		// Request for Instance/VM Id
		Logger.get().info("Requesting for Instance Id");
		cloudUniqueId = CloudEnvironment.get().providerDelegate().getInstanceId();
		LOG.debug("Using Instance Id: " + cloudUniqueId);

		// Set WKA

		wkaHost = env.wkaHost();
		wkaInboundPort = env.wkaPort();

		LOG.debug("Using WKA: " + wkaHost.getHostAddress() + ":" + wkaInboundPort);

		if (logClusterNodesAtIntervals) {
			Scheduler.getDefaultExecutor().scheduleWithFixedDelay((() -> {
				LOG.debug("Available cluster nodes: " + getNodesAsJson());
			}), 1, 10, TimeUnit.SECONDS);
		}

		LOG.debug("Starting Cluster server ..");

		CompletableFuture<Void> future = new CompletableFuture<>();

		// Start cluster server
		server = Server.get(getClusteringAddress(), getInboundPort());
		server.start();

		BaseEvent.one(ServerStartEvent.class, evt -> {
			Logger.get().debug("Cluster server started on: " + evt.getServer().host());
			future.complete(null);
		});

		return future;
	}

	/**
	 * This function is used to indicate that this node should be taken out of
	 * service from the cluster
	 */
	@Override
	public void stop() {

		// Delete Temp File: (Irrespective that temp file set to deleteOnExit)
		try {
			Files.deleteIfExists(Paths.get(getTempFile()));
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		LOG.info("Requesting to leave the cluster ..");

		NodeLeaveResult result = Client.get(getMasterNodeId()).execute(MasterFunction.CLUSTER_LEAVE,
				new NodeLeaveRequest(), NodeLeaveResult.class).join();

		if (!result.isSuccess()) {
			return;
		}

		// stop cluster server
		if(server.isOpen()) {
			getServer().stop();
		}
	}

	@Override
	public Map<Short, BaseNodeSpec> getNodes() {
		return nodes;
	}

	@Override
	public String getNodesAsJson() {

		StringBuilder sb = new StringBuilder();
		sb.append("[");

		List<BaseNodeSpec> v = Lists.newArrayList(getNodes().values());

		for (int i = 0; i < v.size(); i++) {

			BaseNodeSpec s = v.get(i);

			sb.append("\n{").append("address: ").append(s.getRemoteAddress() + ":" + s.getInboundPort()).append(",")
					.append("\t roles: ").append(s.getRoles()).append(",").append("\t state: ").append(s.getState())
					.append(",").append("\t auto: ")
					.append(NodeRole.getDelegate().getMasterRole().getAutoProvisionedNodes().containsKey(s.getId()))

					.append("}");

			if (i < v.size() - 1) {
				sb.append(", ");
			}

		}

		sb.append("]");
		return sb.toString();
	}

	private static String getTempFile() {

		String tempFile = System.getProperty("java.io.tmpdir") + Platform.getPlatformPrefix() + "~"
				+ CloudEnvironment
				.get()
				.clusteringHost()
				.getHostAddress()
				.replace(".", "-") + ".tmp";

		return tempFile;
	}

}
