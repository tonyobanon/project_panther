package com.re.paas.internal.errors;

import com.re.paas.api.errors.Error;

public enum ParameterError implements Error {

	EMPTY_PARAMETER(5, "Empty Parameter"),
	INVALID_PARAMETER(10, "Invalid Parameter");

	private boolean isFatal;
	private int code;
	private String message;

	private ParameterError(Integer code, String message) {
		this(code, message, false);
	}
	
	private ParameterError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
    public String namespace() {
    	return "parameter";
    }
	  
	public static ParameterError from(int value) {

		switch (value) {
		
		case 5:
			return ParameterError.EMPTY_PARAMETER;
			
		case 10:
			return ParameterError.INVALID_PARAMETER;
			
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
