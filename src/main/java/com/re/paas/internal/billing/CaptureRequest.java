package com.re.paas.internal.billing;

import java.math.BigDecimal;

public class CaptureRequest {
	
	private String reference;
	private String originalExtReference;
	
	private String authCode;

	private String currency;
	private BigDecimal amount;

	public String getReference() {
		return reference;
	}

	public CaptureRequest setReference(String reference) {
		this.reference = reference;
		return this;
	}
	
	public String getOriginalExtReference() {
		return originalExtReference;
	}

	public CaptureRequest setOriginalExtReference(String originalExtReference) {
		this.originalExtReference = originalExtReference;
		return this;
	}

	public String getAuthCode() {
		return authCode;
	}

	public CaptureRequest setAuthCode(String authCode) {
		this.authCode = authCode;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public CaptureRequest setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public CaptureRequest setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

}
