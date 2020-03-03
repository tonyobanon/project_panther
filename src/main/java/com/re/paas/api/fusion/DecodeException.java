package com.re.paas.api.fusion;

public class DecodeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DecodeException(String message) {
		super(message);
	}

	public DecodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DecodeException() {
	}
}