package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.cryto.AbstractCryptoAdapterDelegate;
import com.re.paas.api.cryto.CryptoAdapter;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class CryptoAdapterLocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.CRYPTO_ADAPTER;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Adapter");
	}

	@Override
	public Class<?> classType() {
		return CryptoAdapter.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractCryptoAdapterDelegate.class;
	}
	
}
