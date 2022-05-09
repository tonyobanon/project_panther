package com.re.paas.api.crytography;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractCryptoAdapterDelegate extends AbstractAdapterDelegate<CryptoProvider, CryptoAdapter> {
	
	public abstract CryptoProvider getProvider();

	@Override
	public final Class<?> getLocatorClassType() {
		return CryptoAdapter.class;
	}
	
}
