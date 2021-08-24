package com.re.paas.api.runtime;

public class UnknownIdentityException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnknownIdentityException() {
	}
	
	@Override
	public String getMessage() {
		return "Unknown Identity";
	}

}
