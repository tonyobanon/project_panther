package com.re.paas.api.infra.cache;

public class CacheException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CacheException(String msg) {
		super(msg);
	}
	
	public CacheException(Throwable throwable) {
		super(throwable);
	}
}
