package com.re.paas.internal.infra.filesystem;

import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;

public class FileSystemAdapterConfig extends AdapterConfig {
	
	public FileSystemAdapterConfig() {
		this(false);
	}

	public FileSystemAdapterConfig(boolean load) {
		super(AdapterType.FILE_SYSTEM);
		if(load) {
			this.load();
		}
	}
}
