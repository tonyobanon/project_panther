package com.re.paas.api.clustering.master;

import com.re.paas.api.clustering.AbstractRequest;

public class NodeProvisioningRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private boolean master;
	private NodeProvisioningReason reason;
	
	public NodeProvisioningRequest() {
	}

	public Boolean getMaster() {
		return master;
	}

	public NodeProvisioningRequest setMaster(Boolean master) {
		this.master = master;
		return this;
	}

	public NodeProvisioningReason getReason() {
		return reason;
	}

	public NodeProvisioningRequest setReason(NodeProvisioningReason reason) {
		this.reason = reason;
		return this;
	}

	public static enum NodeProvisioningReason {
		LOAD_BALANCING
	}
	
}
