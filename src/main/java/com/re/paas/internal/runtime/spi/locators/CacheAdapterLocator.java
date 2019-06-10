package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheAdapter;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class CacheAdapterLocator extends BaseSPILocator {

	@Override
	public SpiType spiType() {
		return SpiType.CACHE_ADAPTER;
	}

	@Override
	public Class<? extends Resource> classType() {
		return CacheAdapter.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractCacheAdapterDelegate.class;
	}

}
