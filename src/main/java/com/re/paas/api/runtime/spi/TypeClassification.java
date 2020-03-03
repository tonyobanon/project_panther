package com.re.paas.api.runtime.spi;

public enum TypeClassification {

	ACTIVE_RESOURCE(true, true),

	ACTIVE_DELEGATE(false, true),

	OPEN(false, false);

	private final Boolean trustedResource;
	private final Boolean trustedDelegate;

	private TypeClassification(Boolean trustedResource, Boolean trustedDelegate) {
		this.trustedResource = trustedResource;
		this.trustedDelegate = trustedDelegate;
	}

	public Boolean requiresTrustedResource() {
		return trustedResource;
	}

	public Boolean requiresTrustedDelegate() {
		return trustedDelegate;
	}

}
