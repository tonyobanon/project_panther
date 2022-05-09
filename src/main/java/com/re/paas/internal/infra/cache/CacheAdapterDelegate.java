package com.re.paas.internal.infra.cache;

import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.runtime.spi.ShutdownPhase;

public class CacheAdapterDelegate extends AbstractCacheAdapterDelegate {

	private CacheFactory<String, Object> factory;

	@Override
	public Boolean load(LoadPhase phase) {
		
		this.factory = getAdapter().getResource(getConfig().getFields());

		this.factory.initialize();
		
		return true;
	}

	@Override
	public CacheFactory<String, Object> getCacheFactory() {
		return this.factory;
	}


	@Override
	public void shutdown(ShutdownPhase phase) {
		this.factory.shutdown();
	}

	@BlockerTodo
	@Override
	public void migrate(CacheFactory<String, Object> outgoing, BiConsumer<Integer, String> listener) {

		// Move all entries

		// Shutdown outgoing
	}

}
