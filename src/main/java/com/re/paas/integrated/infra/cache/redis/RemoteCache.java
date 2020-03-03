package com.re.paas.integrated.infra.cache.redis;

import java.util.concurrent.CompletableFuture;

import com.re.paas.api.infra.cache.AbstractCache;
import com.re.paas.api.infra.cache.AbstractCodec;
import com.re.paas.api.infra.cache.DefaultCodec;

public abstract class RemoteCache extends AbstractCache {

	private static final AbstractCodec DEFAULT_CODEC = new DefaultCodec();
	private final RemoteCacheFactory factory;

	public RemoteCache(RemoteCacheFactory factory) {
		this.factory = factory;
	}

	protected abstract String getInstanceId();

	protected AbstractCodec getCodec() {
		return DEFAULT_CODEC;
	}
	
	protected void startOperation() {
		if (getInstanceId() != null) {
			factory.incrStack(getInstanceId());
		}
	}

	protected void finishOperation() {
		if (getInstanceId() != null) {
			factory.decrStack(getInstanceId());
		}
	}
	
	abstract CompletableFuture<Void> close();
	
}
