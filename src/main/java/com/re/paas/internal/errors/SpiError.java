package com.re.paas.internal.errors;

import com.re.paas.api.errors.Error;

public enum SpiError implements Error {
	
	APPLICATION_IS_CURRENTLY_IN_USE(5, "Application: {ref1} is currently in use");

	private boolean isFatal = true;
	private int code;
	private String message;

	private SpiError(Integer code, String message) {
		this(code, message, true);
	}

	private SpiError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
	public String namespace() {
		return "application";
	}

	public static SpiError from(int value) {

		switch (value) {

		case 5:
			return SpiError.APPLICATION_IS_CURRENTLY_IN_USE;
			
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
