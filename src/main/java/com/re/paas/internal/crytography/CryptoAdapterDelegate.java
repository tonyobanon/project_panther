package com.re.paas.internal.crytography;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.crytography.AbstractCryptoAdapterDelegate;
import com.re.paas.api.crytography.CryptoProvider;

public class CryptoAdapterDelegate extends AbstractCryptoAdapterDelegate {

	private CryptoProvider provider;

	@Override
	public Boolean load(LoadPhase pahse) {
		this.provider = getAdapter().getResource(getConfig().getFields());
		return true;
	}

	@Override
	public CryptoProvider getProvider() {
		return this.provider;
	}
}
