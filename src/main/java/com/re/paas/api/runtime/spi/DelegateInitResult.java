package com.re.paas.api.runtime.spi;

import com.re.paas.api.adapters.AdapterType;

public enum DelegateInitResult {

	SUCCESS,

	FAILURE,

	PENDING_ADAPTER_CONFIGURATION;

	private String errorMessage;
	private AdapterType adapterType;

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public AdapterType getAdapterType() {
		return adapterType;
	}

	public DelegateInitResult setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

	public DelegateInitResult setAdapterType(AdapterType adapterType) {
		this.adapterType = adapterType;
		return this;
	}

}
