package com.re.paas.api.clustering;

import java.io.Serializable;

public abstract class AbstractRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Short nodeId;
	private Short clientId;
	
	public Short getNodeId() {
		return nodeId;
	}
	
	public AbstractRequest setNodeId(Short nodeId) {
		this.nodeId = nodeId;
		return this;
	}
	
	public Short getClientId() {
		return clientId;
	}
	
	public AbstractRequest setClientId(Short clientId) {
		this.clientId = clientId;
		return this;
	}
	
}
