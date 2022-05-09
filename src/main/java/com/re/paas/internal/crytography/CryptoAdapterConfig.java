package com.re.paas.internal.crytography;

import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;

public class CryptoAdapterConfig extends AdapterConfig {
	
	public CryptoAdapterConfig() {
		this(false);
	}

	public CryptoAdapterConfig(boolean load) {
		super(AdapterType.CRYPTO);
		if (load) {
			this.load();
		}
	}
	
}
