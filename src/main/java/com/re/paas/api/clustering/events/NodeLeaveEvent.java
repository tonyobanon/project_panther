package com.re.paas.api.clustering.events;

import com.re.paas.api.events.BaseEvent;

public class NodeLeaveEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private Short nodeId;
	
	@Override
	public String name() {
		return "NodeLeaveEvent";
	}

	public Short getNodeId() {
		return nodeId;
	}

	public NodeLeaveEvent setNodeId(Short nodeId) {
		this.nodeId = nodeId;
		return this;
	}
}
