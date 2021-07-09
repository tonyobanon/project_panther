package com.re.paas.internal.infra.cache;

import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheFactory;

public class CacheAdapterDelegate extends AbstractCacheAdapterDelegate {

	private static CacheFactory<String, Object> factory;

	@Override
	public Boolean load(LoadPhase phase) {
		
		assert phase == LoadPhase.START;
		
		getCacheFactory(true);
		return true;
	}

	@Override
	public CacheFactory<String, Object> getCacheFactory(boolean loadConfigFile) {

		if (factory != null && !loadConfigFile) {
			return factory;
		}

		CacheFactory<String, Object> factory = getAdapter().cacheFactory(getConfig().getFields());

		CacheAdapterDelegate.factory = factory;
		return factory;
	}

	@Override
	public void shutdown() {
		if (factory != null) {
			factory.shutdown();
		}
	}

	@BlockerTodo
	@Override
	public void migrate(CacheFactory<String, Object> outgoing, BiConsumer<Integer, String> listener) {

		// Move all entries

		// Shutdown outgoing
	}

}
