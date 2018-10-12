package com.re.paas.api.clustering.protocol;

import com.re.paas.api.clustering.AbstractRequest;

public abstract class AbstractNodeRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private String remoteAddress;
	
	public String getRemoteAddress() {
		return remoteAddress;
	}

	public AbstractNodeRequest setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
		return this;
	}
	
}
