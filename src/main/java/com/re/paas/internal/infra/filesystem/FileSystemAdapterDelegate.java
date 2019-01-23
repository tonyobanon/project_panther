package com.re.paas.internal.infra.filesystem;

import java.nio.file.spi.FileSystemProvider;

import com.re.paas.api.infra.filesystem.AbstractFileSystemAdapterDelegate;
import com.re.paas.api.infra.filesystem.FileSystemAdapter;

public class FileSystemAdapterDelegate extends AbstractFileSystemAdapterDelegate {

	@Override
	public Object load() {
		
		FileSystemAdapterConfig config = (FileSystemAdapterConfig) getConfig();
		
		// Acquire lock
		FileSystemProviderImpl.acquireLock();

		// Update file system provider

		AbstractFileSystemAdapterDelegate delegate = FileSystemAdapter.getDelegate();

		FileSystemAdapter adapter = delegate.getAdapter(config.getAdapterName());
		FileSystemProvider provider = adapter.fileSystemProvider(config.getFields());

		FileSystemProviderImpl.setProvider(provider);

		// Release lock
		FileSystemProviderImpl.releaseLock();
		
		return true;
	}

}
