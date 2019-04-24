package com.re.paas.api.infra.filesystem;

import java.nio.file.spi.FileSystemProvider;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractFileSystemAdapterDelegate extends AbstractAdapterDelegate<FileSystemProvider, FileSystemAdapter> {
	
	@Override
	public final boolean requiresMigration() {
		return true;
	}
	
}
