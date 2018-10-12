package com.re.paas.internal.clustering.objectmodels;

import com.re.paas.api.clustering.protocol.AbstractNodeRequest;

public class NodeLeaveRequest extends AbstractNodeRequest {

	private static final long serialVersionUID = 1L;

	private Short nodeId;

	public Short getNodeId() {
		return nodeId;
	}

	public NodeLeaveRequest setNodeId(Short nodeId) {
		this.nodeId = nodeId;
		return this;
	}

}
