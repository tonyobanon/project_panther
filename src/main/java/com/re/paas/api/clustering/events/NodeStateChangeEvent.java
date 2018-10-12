package com.re.paas.api.clustering.events;

import com.re.paas.api.clustering.classes.NodeState;
import com.re.paas.api.events.BaseEvent;

public class NodeStateChangeEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private Short nodeId;
	private NodeState newState;
	
	@Override
	public String name() {
		return "NodeStateChangeEvent";
	}

	public Short getNodeId() {
		return nodeId;
	}

	public NodeStateChangeEvent setNodeId(Short nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public NodeState getNewState() {
		return newState;
	}

	public NodeStateChangeEvent setNewState(NodeState newState) {
		this.newState = newState;
		return this;
	}
}
