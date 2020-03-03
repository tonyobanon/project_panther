package com.re.paas.internal.clustering.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.annotations.develop.BlockerTodo;

public class MemberSetupResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean isSuccess;
	private String message;

	private final Map<AdapterType, Exception> adapterIngestionErrors = new HashMap<>();
	
	public boolean isSuccess() {
		return isSuccess;
	}

	public MemberSetupResult setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public MemberSetupResult setMessage(String message) {
		this.message = message;
		return this;
	}

	public Map<AdapterType, Exception> getErrors() {
		return adapterIngestionErrors;
	}
	
	public void addError(AdapterType type, Exception error) {
		this.adapterIngestionErrors.put(type, error);
	}
	
	@BlockerTodo("Based on the various error maps in this object, construct a relevant error")
	public Exception getError() {
		
		if (isSuccess) {
			return null;
		}
		
		return new RuntimeException();
	}
	
}
