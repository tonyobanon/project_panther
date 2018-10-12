package com.re.paas.internal.billing;

public class InvoiceAuthorizationResult {

	private boolean isSuccess;
	private String authCode;
	
	private boolean isRedirectShopper;
	private String redirectUrl;
	
	
	public static InvoiceAuthorizationResult failed(String authCode) {
		return new InvoiceAuthorizationResult();
	}
	
	public static InvoiceAuthorizationResult success(String authCode) {
		return new InvoiceAuthorizationResult().setIsSuccess(true).setAuthCode(authCode);
	}
	
	public static InvoiceAuthorizationResult redirect(String redirectUrl) {
		return new InvoiceAuthorizationResult().setRedirectShopper(true).setRedirectUrl(redirectUrl);
	}
	
	public boolean getIsSuccess() {
		return isSuccess;
	}

	public InvoiceAuthorizationResult setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
		return this;
	}

	public String getAuthCode() {
		return authCode;
	}

	public InvoiceAuthorizationResult setAuthCode(String authCode) {
		this.authCode = authCode;
		return this;
	}

	public boolean isRedirectShopper() {
		return isRedirectShopper;
	}

	public InvoiceAuthorizationResult setRedirectShopper(boolean isRedirectShopper) {
		this.isRedirectShopper = isRedirectShopper;
		return this;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public InvoiceAuthorizationResult setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
		return this;
	}
	
}
