package com.re.paas.api.infra.filesystem;

import java.nio.file.FileSystem;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractFileSystemAdapterDelegate extends AbstractAdapterDelegate<FileSystem, FileSystemAdapter> {
	
	@Override
	public final boolean requiresMigration() {
		return true;
	}
	
	@Override
	public final Class<?> getLocatorClassType() {
		return FileSystemAdapter.class;
	}
	
}
