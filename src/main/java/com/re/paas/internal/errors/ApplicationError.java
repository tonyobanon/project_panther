package com.re.paas.internal.errors;

import com.re.paas.api.errors.Error;

public enum ApplicationError implements Error {
	
	APPLICATION_IS_RUNNING_IN_A_JAR_FILE(5, "Application is running in a Jar file"),

	COULD_NOT_FIND_SUITABLE_HTTP_ADDRESS(10, "Could not find a suitable http address to use for this host"),
	COULD_NOT_FIND_SUITABLE_CLUSTERING_ADDRESS(15, "Could not find a suitable clustering address to use for this host"),
	
	SERVICE_PROVIDER_CLASS_NOT_CONCRETE_IMPL(20, "The Service Provider Class: " + "{ref1}" + " is not a subtype of {ref2}");
	
	private boolean isFatal = true;
	private int code;
	private String message;

	private ApplicationError(Integer code, String message) {
		this(code, message, true);
	}

	private ApplicationError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}
	
	@Override
	public String namespace() {
		return "application";
	}

	public static ApplicationError from(int value) {

		switch (value) {

		case 5:
			return ApplicationError.APPLICATION_IS_RUNNING_IN_A_JAR_FILE;
			
		case 10:
			return ApplicationError.COULD_NOT_FIND_SUITABLE_HTTP_ADDRESS;
			
		case 15:
			return ApplicationError.COULD_NOT_FIND_SUITABLE_CLUSTERING_ADDRESS;
			
		case 20:
			return ApplicationError.SERVICE_PROVIDER_CLASS_NOT_CONCRETE_IMPL;
		
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
