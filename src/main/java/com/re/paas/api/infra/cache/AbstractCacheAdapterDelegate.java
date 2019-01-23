package com.re.paas.api.infra.cache;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractCacheAdapterDelegate extends AbstractAdapterDelegate<CacheAdapter> {
	
	public abstract CacheFactory<String, Object> getCacheFactory(boolean load);
	
	public CacheFactory<String, Object> getCacheFactory() {
		return getCacheFactory(false);
	}
	
	@Override
	public final boolean inMemory() {
		return true;
	}
	
}
