package com.re.paas.internal.infra.database;

import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;

public class DatabaseAdapterConfig extends AdapterConfig {
	
	public DatabaseAdapterConfig() {
		this(false);
	}

	public DatabaseAdapterConfig(boolean load) {
		super(AdapterType.DATABASE);
		if (load) {
			this.load();
		}
	}
	
}
