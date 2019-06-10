package com.re.paas.internal.billing;

public class AuthorizationResult {

	private String pspReference;

	PaymentResultCode resultCode;

	private String authCode;
	private String refusalReason;

	private Boolean is3dSecureOffered;
	private Authorise3dSecureRequest Authorise3dRequest;
	
	private Boolean isError;
	private String errorMessage;
	
	


	public String getPspReference() {
		return pspReference;
	}

	public AuthorizationResult setPspReference(String pspReference) {
		this.pspReference = pspReference;
		return this;
	}

	public PaymentResultCode getResultCode() {
		return resultCode;
	}

	public AuthorizationResult setResultCode(PaymentResultCode resultCode) {
		this.resultCode = resultCode;
		return this;
	}

	public String getAuthCode() {
		return authCode;
	}

	public AuthorizationResult setAuthCode(String authCode) {
		this.authCode = authCode;
		return this;
	}

	public String getRefusalReason() {
		return refusalReason;
	}

	public AuthorizationResult setRefusalReason(String refusalReason) {
		this.refusalReason = refusalReason;
		return this;
	}

	public boolean isIs3dSecureOffered() {
		return is3dSecureOffered;
	}

	public AuthorizationResult setIs3dSecureOffered(boolean is3dSecureOffered) {
		this.is3dSecureOffered = is3dSecureOffered;
		return this;
	}

	public Authorise3dSecureRequest getAuthorise3dRequest() {
		return Authorise3dRequest;
	}

	public AuthorizationResult setAuthorise3dRequest(Authorise3dSecureRequest authorise3dRequest) {
		Authorise3dRequest = authorise3dRequest;
		return this;
	}
	
	public Boolean getIsError() {
		return isError;
	}

	public AuthorizationResult setIsError(Boolean isError) {
		this.isError = isError;
		return this;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public AuthorizationResult setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}

}
