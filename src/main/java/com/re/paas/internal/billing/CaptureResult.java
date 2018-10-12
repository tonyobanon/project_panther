package com.re.paas.internal.billing;

public class CaptureResult {

	private String reference;

	private boolean isError;
	private String errorMessage;
	
	public String getReference() {
		return reference;
	}

	public CaptureResult setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public boolean isError() {
		return isError;
	}

	public CaptureResult setError(boolean isError) {
		this.isError = isError;
		return this;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public CaptureResult setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

}
