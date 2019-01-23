package com.re.paas.internal.infra.cache;

import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.infra.cache.CacheFactory;

public class CacheAdapterDelegate extends AbstractCacheAdapterDelegate {

	private static CacheFactory<String, Object> factory;
	
	@Override
	public Object load() {
		return getCacheFactory(true) != null;
	}

	@Override
	public CacheFactory<String, Object> getCacheFactory(boolean load) {
		
		if(factory != null && !load) {
			return factory;
		}
		
		CacheAdapterConfig config = (CacheAdapterConfig) getConfig();
		
		CacheAdapter adapter = getAdapter(config.getAdapterName());
		CacheFactory<String, Object> factory = adapter.cacheFactory(config.getFields());
		
		CacheAdapterDelegate.factory = factory;
		return factory;
	}
	
}
