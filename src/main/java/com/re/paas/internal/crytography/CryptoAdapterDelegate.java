package com.re.paas.internal.crytography;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.crytography.AbstractCryptoAdapterDelegate;
import com.re.paas.api.crytography.CryptoProvider;

public class CryptoAdapterDelegate extends AbstractCryptoAdapterDelegate {

	private static CryptoProvider provider;

	@Override
	public Boolean load(LoadPhase pahse) {
		getProvider(true);
		return true;
	}

	@Override
	public CryptoProvider getProvider(boolean loadConfigFile) {

		if (provider != null && !loadConfigFile) {
			return provider;
		}

		CryptoProvider provider = getAdapter().getProvider(getConfig().getFields());

		CryptoAdapterDelegate.provider = provider;
		return provider;
	}
}
