package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class CacheAdapterLocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.CACHE_ADAPTER;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Adapter");
	}

	@Override
	public Class<?> classType() {
		return CacheAdapter.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractCacheAdapterDelegate.class;
	}
	
}
