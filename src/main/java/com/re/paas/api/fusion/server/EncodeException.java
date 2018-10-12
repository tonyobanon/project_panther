package com.re.paas.api.fusion.server;

public class EncodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EncodeException(String message) {
	    super(message);
	  }

	  public EncodeException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public EncodeException() {
	  }
	}