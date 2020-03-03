package com.re.paas.api.infra.cache;

import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.annotations.Final;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.forms.Form;
import com.re.paas.api.runtime.spi.SpiType;

public interface CacheAdapter extends Adapter<CacheFactory<String, Object>> {

	public static AbstractCacheAdapterDelegate getDelegate() {
		return Singleton.get(AbstractCacheAdapterDelegate.class);
	}
	
	default CacheFactory<String, Object> cacheFactory(Map<String, String> fields) {
		return getResource(fields);
	}
	
	default Form initForm() {
		return null;
	}
	
	@Override
	default AdapterType getType() {
		return AdapterType.CACHE;
	}
	
	@Override
	@Final
	default SpiType getSpiType() {
		return SpiType.CACHE_ADAPTER;
	}
}
