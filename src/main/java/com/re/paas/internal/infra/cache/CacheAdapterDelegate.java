package com.re.paas.internal.infra.cache;

import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.runtime.spi.ShutdownPhase;

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

		factory = getAdapter().cacheFactory(getConfig().getFields());
		factory.initialize();
		
		return factory;
	}


	@BlockerTodo("Deleting the entries (just like that) will adversely affect tasks that depend on on cached entries")
	@Override
	public void shutdown(ShutdownPhase phase) {
		
		assert factory != null;
		
		factory.shutdown(phase);
	}

	@BlockerTodo
	@Override
	public void migrate(CacheFactory<String, Object> outgoing, BiConsumer<Integer, String> listener) {

		// Move all entries

		// Shutdown outgoing
	}

}
