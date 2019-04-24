package com.re.paas.api.infra.cache;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractCacheAdapterDelegate extends AbstractAdapterDelegate<CacheFactory<String, Object>, CacheAdapter> {
	
	public abstract CacheFactory<String, Object> getCacheFactory(boolean loadConfigFile);
	
	public CacheFactory<String, Object> getCacheFactory() {
		return getCacheFactory(false);
	}
	
	@Override
	public boolean requiresMigration() {
		return true;
	}

	@Override
	public final boolean inMemory() {
		return true;
	}
	
}
