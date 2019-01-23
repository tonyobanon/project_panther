package com.re.paas.api.infra.filesystem;

import java.nio.file.spi.FileSystemProvider;
import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.designpatterns.Singleton;

public interface FileSystemAdapter extends Adapter {
	
	public static AbstractFileSystemAdapterDelegate getDelegate() {
		return Singleton.get(AbstractFileSystemAdapterDelegate.class);
	}
	
	FileSystemProvider fileSystemProvider(Map<String, String> fields);
	
	@Override
	default AdapterType getType() {
		return AdapterType.FILE_SYSTEM;
	}
}
