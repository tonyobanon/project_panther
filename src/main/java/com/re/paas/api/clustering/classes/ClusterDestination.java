package com.re.paas.api.clustering.classes;

import java.net.InetSocketAddress;

public enum ClusterDestination {

	OTHER_NODES, ALL_NODES, SPECIFIC_NODE;

	private Object destination;

	public Object getDestination() {
		return destination;
	}
	
	public <T> T getDestination(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		T t = (T) destination;
		return t;
	}

	public ClusterDestination setDestination(Short nodeId) {
		this.destination = nodeId;
		return this;
	}
	
	public ClusterDestination setDestination(InetSocketAddress addr) {
		this.destination = addr;
		return this;
	}
	
	public static ClusterDestination spec(BaseNodeSpec spec) {
		return ClusterDestination.SPECIFIC_NODE.setDestination(ClusteringUtils.getInetSocketAddress(spec));
	}
	
}
