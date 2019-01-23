package com.re.paas.internal.crypto;

import com.re.paas.api.cryto.AbstractCryptoAdapterDelegate;
import com.re.paas.api.cryto.CryptoAdapter;
import com.re.paas.api.cryto.CryptoProvider;

public class CryptoAdapterDelegate extends AbstractCryptoAdapterDelegate {

	private static CryptoProvider provider;
	
	@Override
	public Object load() {
		return getProvider(true) != null;
	}

	@Override
	public CryptoProvider getProvider(boolean loadConfigFile) {
		
		if(provider != null && !loadConfigFile) {
			return provider;
		} 
		
		CryptoAdapterConfig config = (CryptoAdapterConfig) getConfig();
		
		CryptoAdapter adapter = getAdapter(config.getAdapterName());
		CryptoProvider provider = adapter.getProvider(config.getFields());
		
		CryptoAdapterDelegate.provider = provider;
		return provider;
	}

}
