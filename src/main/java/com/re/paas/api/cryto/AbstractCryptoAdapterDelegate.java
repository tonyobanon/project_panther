package com.re.paas.api.cryto;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractCryptoAdapterDelegate extends AbstractAdapterDelegate<CryptoAdapter> {
	
	public abstract CryptoProvider getProvider(boolean loadConfigFile);
	
	public CryptoProvider getProvider() {
		return getProvider(false);
	}

}
