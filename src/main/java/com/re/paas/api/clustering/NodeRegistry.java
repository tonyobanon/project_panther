package com.re.paas.api.clustering;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.designpatterns.Singleton;

public interface NodeRegistry {

	public static NodeRegistry get() {
		return Singleton.get(NodeRegistry.class);
	}
	
	void setNodeId(Short nodeId);

	Short getNodeId();
	
	void setMasterNodeId(Short nodeId);

	Short getMasterNodeId();

	String clusterName();

	Integer getInboundPort();

	Boolean isAutoScalingEnabled();

	String getCloudUniqueId();

	String getName();

	InetAddress getClusteringAddress();

	InetAddress getWkaHost();
	
	Integer getWkaInboundPort();

	Server getServer();
	
	CompletableFuture<Void> start();

	void stop();

	Map<Short, BaseNodeSpec> getNodes();

	String getNodesAsJson();
	
}
