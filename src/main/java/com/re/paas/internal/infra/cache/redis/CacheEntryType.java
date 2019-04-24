package com.re.paas.internal.infra.cache.redis;

public enum CacheEntryType { 
	
	PRIMITIVE(1), SET(2), MAP(3);
	
	private final int type;
	
	private CacheEntryType(int type) {
		this.type = type;
	}
	
	public Integer getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return getType().toString();
	}
}
