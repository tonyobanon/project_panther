package com.re.paas.internal.infra.cache;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheFactory;

public class CacheAdapterDelegate extends AbstractCacheAdapterDelegate {

	private static CacheFactory<String, Object> factory;

	@Override
	public Object load(LoadPhase phase) {
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

}
