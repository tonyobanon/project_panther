package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.cryto.AbstractCryptoAdapterDelegate;
import com.re.paas.api.cryto.CryptoAdapter;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class CryptoAdapterLocator extends BaseSPILocator {

	@Override
	public SpiType spiType() {
		return SpiType.CRYPTO_ADAPTER;
	}

	@Override
	public Class<? extends Resource> classType() {
		return CryptoAdapter.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractCryptoAdapterDelegate.class;
	}
	
}
