package com.re.paas.api.crytography;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractCryptoAdapterDelegate extends AbstractAdapterDelegate<CryptoProvider, CryptoAdapter> {
	
	public abstract CryptoProvider getProvider(boolean loadConfigFile);
	
	public CryptoProvider getProvider() {
		return getProvider(false);
	}

	@Override
	public final Class<?> getLocatorClassType() {
		return CryptoAdapter.class;
	}
	
}
