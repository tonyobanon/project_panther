package com.re.paas.internal.infra.cache;

import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;

public class CacheAdapterConfig extends AdapterConfig {

	public CacheAdapterConfig() {
		this(false);
	}

	public CacheAdapterConfig(boolean load) {
		super(AdapterType.CACHE);
		if (load) {
			this.load();
		}
	}
}
