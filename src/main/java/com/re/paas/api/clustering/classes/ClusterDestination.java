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

	public ClusterDestination setDestination(Short memberId) {
		this.destination = memberId;
		return this;
	}
	
	public ClusterDestination setDestination(InetSocketAddress addr) {
		this.destination = addr;
		return this;
	}

}
