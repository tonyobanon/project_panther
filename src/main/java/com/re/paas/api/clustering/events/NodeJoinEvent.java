package com.re.paas.api.clustering.events;

import java.util.ArrayList;
import java.util.Collection;

import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.events.BaseEvent;

public class NodeJoinEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;
	
	private Collection<BaseNodeSpec> nodes = new ArrayList<>();

	public Collection<BaseNodeSpec> getNodes() {
		return nodes;
	}

	public NodeJoinEvent withNodes(Collection<BaseNodeSpec> nodes) {
		this.nodes.addAll(nodes);
		return this;
	}
	
	public NodeJoinEvent withNode(BaseNodeSpec node) {
		this.nodes.add(node);
		return this;
	}

	@Override
	public String name() {
		return "NodeJoinEvent";
	}
}
