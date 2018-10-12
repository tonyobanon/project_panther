package com.re.paas.internal.clustering.objectmodels;

import java.io.Serializable;

public class NodeLeaveResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isSuccess;
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public NodeLeaveResult setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
		return this;
	}
}
