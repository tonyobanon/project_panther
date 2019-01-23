package com.re.paas.api.infra.cache;

public enum PersistenceType {
	
	VOLATILE(30), SHORT_LIVED(3600), LONG_LIVED(2592000), PERSISTENT(null);
	
	private final Integer expiry;
	
	private PersistenceType(Integer expiry) {
		this.expiry = expiry;
	}

	public Integer getExpiry() {
		return expiry;
	}

}