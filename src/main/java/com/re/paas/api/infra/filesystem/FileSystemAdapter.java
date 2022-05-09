package com.re.paas.api.infra.filesystem;

import java.nio.file.FileSystem;

import com.re.paas.api.Adapter;
import com.re.paas.api.Singleton;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.runtime.spi.SpiType;

public interface FileSystemAdapter extends Adapter<FileSystem> {
	
	public static AbstractFileSystemAdapterDelegate getDelegate() {
		return Singleton.get(AbstractFileSystemAdapterDelegate.class);
	}

	@Override
	default AdapterType getType() {
		return AdapterType.FILE_SYSTEM;
	}
	
	@Override
	default SpiType getSpiType() {
		return SpiType.FILESYSTEM_ADAPTER;
	}
	
}
