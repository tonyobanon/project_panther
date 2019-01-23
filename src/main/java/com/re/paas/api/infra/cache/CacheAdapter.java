package com.re.paas.api.infra.cache;

import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.designpatterns.Singleton;

public interface CacheAdapter extends Adapter {

	public static AbstractCacheAdapterDelegate getDelegate() {
		return Singleton.get(AbstractCacheAdapterDelegate.class);
	}
	
	CacheFactory<String, Object> cacheFactory(Map<String, String> fields);
	
	@Override
	default AdapterType getType() {
		return AdapterType.CACHE;
	}
}
