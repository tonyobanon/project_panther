package com.re.paas.api.infra.cache;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractCache implements Cache<String, Object> {

	@Override
	public CompletableFuture<Object> set(String key, Object value, Long maxIdle) {
		return this.set(key, value).thenCompose(r -> this.invalidate(key, maxIdle).thenApply(b -> r));
	}
	
	@Override
	public CompletableFuture<Object> setex(String key, Object value, Long lifespan) {
		return this.set(key, value).thenCompose(r -> this.expire(key, lifespan).thenApply(b -> r));
	}
}
