package com.re.paas.internal.infra.filesystem;

import java.nio.file.FileSystem;
import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.infra.filesystem.AbstractFileSystemAdapterDelegate;

public class FileSystemAdapterDelegate extends AbstractFileSystemAdapterDelegate {

	@Override
	public Boolean load(LoadPhase pahse) {
		
		// Acquire lock
		FileSystemProviderImpl.acquireLock();

		// Update file system provider

		FileSystem fs = getAdapter().fileSystem(getConfig().getFields());

		FileSystemProviderImpl.setFileSystem(fs);
		
		
		// Todo: Based on the current phase, perform an action
		

		// Release lock
		FileSystemProviderImpl.releaseLock();
		
		return true;
	}
	
	@Override
	public void migrate(FileSystem outgoing, BiConsumer<Integer, String> listener) {
		
	}

}
