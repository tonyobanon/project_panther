package com.re.paas.internal.clustering.objectmodels;

import java.io.Serializable;

public class NodeJoinResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isSuccess;
	private String message;

	private Short nodeId;
	private Short masterNodeId;

	public boolean isSuccess() {
		return isSuccess;
	}

	public NodeJoinResult setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public NodeJoinResult setMessage(String message) {
		this.message = message;
		return this;
	}

	public Short getNodeId() {
		return nodeId;
	}

	public NodeJoinResult setNodeId(Short nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public Short getMasterNodeId() {
		return masterNodeId;
	}

	public NodeJoinResult setMasterNodeId(Short masterNodeId) {
		this.masterNodeId = masterNodeId;
		return this;
	}
}
