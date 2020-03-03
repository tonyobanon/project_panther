package com.re.paas.integrated.models.errors;

import com.re.paas.api.errors.Error;

public enum ListablesError implements Error {

	CLIENT_CONTEXT_CREATION_NOT_ALLOWED(5, "Client Context creation is not supported for the specified type");

	private boolean isFatal;
	private int code;
	private String message;

	private ListablesError(Integer code, String message) {
		this(code, message, false);
	}
	
	private ListablesError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
    public String namespace() {
    	return "listables";
    }
	  
	public static ListablesError from(int value) {

		switch (value) {
		
		case 5:
			return ListablesError.CLIENT_CONTEXT_CREATION_NOT_ALLOWED;
			
		default:
			return null;			
		}
	}
	
	@Override
	public boolean isFatal() {
		return isFatal;
	}
	
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
