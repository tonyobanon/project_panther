package com.re.paas.internal.errors;

import com.re.paas.api.errors.Error;

public enum ClusterFunctionError implements Error {

	MAX_NUMBER_OF_FUNCTIONS_REACHED(5, "Maximum number of functions reached");
	
	private boolean isFatal;
	private int code;
	private String message;

	private ClusterFunctionError(Integer code, String message) {
		this(code, message, false);
	}
	
	private ClusterFunctionError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
    public String namespace() {
    	return "cluster-function";
    }
	  
	public static ClusterFunctionError from(int value) {

		switch (value) {
		
		case 5:
			return MAX_NUMBER_OF_FUNCTIONS_REACHED;
			
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
