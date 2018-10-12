package com.re.paas.internal.clustering.objectmodels;

import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.clustering.protocol.AbstractNodeRequest;
import com.re.paas.internal.clustering.NodeRoleHelper;

public class NodeJoinRequest extends AbstractNodeRequest {

	private static final long serialVersionUID = 1L;

	private String clusterName;
	private Integer inboundPort;

	private String nodeName;
	private ClusterCredentials credentials;

	private String roles;

	public String getClusterName() {
		return clusterName;
	}

	public NodeJoinRequest setClusterName(String clusterName) {
		this.clusterName = clusterName;
		return this;
	}

	public Integer getInboundPort() {
		return inboundPort;
	}

	public NodeJoinRequest setInboundPort(Integer inboundPort) {
		this.inboundPort = inboundPort;
		return this;
	}

	public String getNodeName() {
		return nodeName;
	}

	public NodeJoinRequest setNodeName(String nodeName) {
		this.nodeName = nodeName;
		return this;
	}

	public ClusterCredentials getCredentials() {
		return credentials;
	}

	public NodeJoinRequest setCredentials(ClusterCredentials credentials) {
		this.credentials = credentials;
		return this;
	}

	public String getRoles() {
		return roles;
	}

	public NodeJoinRequest setRoles(String roles) {
		this.roles = roles;
		return this;
	}

	public static NodeJoinRequest get() {

		NodeRegistry nodeRegistry = NodeRegistry.get();
		
		return new NodeJoinRequest().setClusterName(nodeRegistry.clusterName())
				.setInboundPort(nodeRegistry.getInboundPort()).setNodeName(nodeRegistry.getName())
				.setCredentials(CloudEnvironment.get().credentials())
				.setRoles(NodeRoleHelper.toString(NodeRole.get().values()));
	}

}
