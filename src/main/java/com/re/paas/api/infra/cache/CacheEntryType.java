package com.re.paas.api.infra.cache;

public enum CacheEntryType {

	PRIMITIVE(1), SET(2), HASH(3);

	private final Integer type;

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

	public static CacheEntryType fromType(String type) {
		
		if(type == null) {
			return null;
		}
		
		return fromType(Integer.parseInt(type));
	}
	
	public static CacheEntryType fromType(Integer type) {
		switch (type) {
		case 1:
			return PRIMITIVE;
		case 2:
			return SET;
		case 3:
			return HASH;
		default:
			throw new IllegalArgumentException("Invalid type: " + type);
		}

	}
}
