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
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.tasks.Scheduler;
import com.re.paas.internal.Platform;
import com.re.paas.internal.cloud.CloudEnvironmentAdapter;
import com.re.paas.internal.clustering.classes.ServerStartEvent;
import com.re.paas.internal.clustering.objectmodels.NodeLeaveRequest;
import com.re.paas.internal.clustering.objectmodels.NodeLeaveResult;
import com.re.paas.internal.errors.ClusteringError;

public class DefaultNodeRegistry implements NodeRegistry {

	private static Short nodeId;

	private static Short masterNodeId;

	private static String name;

	private static String clusterName;

	private static Integer inboundPort;

	private static Boolean autoScalingEnabled;

	private static String cloudUniqueId;

	private static InetAddress clusteringAddress;

	private static InetAddress wkaHost;
	private static Integer wkaInboundPort;

	private static Server server;

	private static Map<Short, BaseNodeSpec> nodes = Maps.newHashMap();

	@Override
	public void setNodeId(Short nodeId) {
		DefaultNodeRegistry.nodeId = nodeId;
	}

	@Override
	public Short getNodeId() {
		return nodeId;
	}

	@Override
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
	public Boolean isAutoScalingEnabled() {
		return autoScalingEnabled;
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

		// Set Cluster Name

		clusterName = ClusterConfig.getInstance().get(ClusterConfig.CLUSTER_NAME);

		if (!ClusterConfig.CLUSTER_NAME_PATTERN.matcher(clusterName).matches()) {
			Exceptions.throwRuntime(
					PlatformException.get(ClusteringError.CLUSTER_NAME_HAS_INCORRECT_FORMAT, clusterName));
		}

		Logger.get().info("Starting node on cluster: " + clusterName);

		// Set node name
		DefaultNodeRegistry.name = CloudEnvironmentAdapter.getResourceName();

		// Get clustering address
		Logger.get().info("Acquiring clustering address");
		clusteringAddress = CloudEnvironment.get().clusteringHost();
		Logger.get().info("Using clustering address: " + CloudEnvironment.get().clusteringHost().getHostAddress());

		// Set Inbound port
		DefaultNodeRegistry.inboundPort = ClusterConfig.getInstance().getInteger(ClusterConfig.CLUSTERING_PORT);
		Logger.get().info("Using port: " + inboundPort);

		autoScalingEnabled = CloudEnvironment.get().canAutoScale();
		Logger.get().info("Auto Scaling is currently " + (autoScalingEnabled ? "enabled" : "disabled"));

		// Request for Instance/VM Id
		Logger.get().info("Requesting for Instance Id");
		cloudUniqueId = CloudEnvironment.get().autoScaleDelegate().getInstanceId();
		Logger.get().info("Using Instance Id: " + cloudUniqueId);

		// Set WKA

		wkaHost = CloudEnvironment.get().wkaHost();
		wkaInboundPort = CloudEnvironment.get().wkaPort();

		Logger.get().info("Using WKA: " + wkaHost.getHostAddress() + ":" + wkaInboundPort);

		if (true) {
			Scheduler.getDefaultExecutor().scheduleWithFixedDelay((() -> {
				Logger.get().debug(getNodesAsJson());
			}), 1, 10, TimeUnit.SECONDS);
		}

		Logger.get().info("Starting Cluster server ..");

		CompletableFuture<Void> future = new CompletableFuture<>();

		// Start cluster server
		server = Server.get(getClusteringAddress(), getInboundPort());
		server.start();

		BaseEvent.one(ServerStartEvent.class, evt -> {
			Logger.get().info("Cluster server started on: " + evt.getServer().host());
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

		Logger.get().info("Requesting to leave the cluster ..");

		NodeLeaveResult result = Client.get(getMasterNodeId()).execute(MasterFunction.CLUSTER_LEAVE,
				new NodeLeaveRequest().setNodeId(getNodeId()), NodeLeaveResult.class).join();

		if (!result.isSuccess()) {
			return;
		}

		// stop cluster server
		getServer().stop();
	}

	@Override
	public Map<Short, BaseNodeSpec> getNodes() {
		return nodes;
	}

	@Override
	public String getNodesAsJson() {

		StringBuilder sb = new StringBuilder();
		sb.append("\n[");

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

		sb.append("\n]");
		return sb.toString();
	}

	private static String getTempFile() {

		String tempFile = System.getProperty("java.io.tmpdir") + Platform.getPlatformPrefix() + "~"
				+ CloudEnvironment.get().clusteringHost().getHostAddress().replace(".", "-") + ".tmp";

		return tempFile;
	}

}
