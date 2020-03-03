package com.re.paas.api.clustering;

import java.io.Serializable;

public abstract class AbstractRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Short memberId;
	private Short clientId;
	
	public Short getMemberId() {
		return memberId;
	}
	
	public AbstractRequest setMemberId(Short memberId) {
		this.memberId = memberId;
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
