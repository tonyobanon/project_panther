package com.re.paas.internal.billing;

public class Authorise3dSecureRequest {

	private String paRequest;
	private String md;
	private String issuerUrl;

	public Authorise3dSecureRequest(String paRequest, String md, String issuerUrl) {
		this.paRequest = paRequest;
		this.md = md;
		this.issuerUrl = issuerUrl;
	}

	public String getPaRequest() {
		return paRequest;
	}

	public Authorise3dSecureRequest setPaRequest(String paRequest) {
		this.paRequest = paRequest;
		return this;
	}

	public String getMd() {
		return md;
	}

	public Authorise3dSecureRequest setMd(String md) {
		this.md = md;
		return this;
	}

	public String getIssuerUrl() {
		return issuerUrl;
	}

	public Authorise3dSecureRequest setIssuerUrl(String issuerUrl) {
		this.issuerUrl = issuerUrl;
		return this;
	}

}
