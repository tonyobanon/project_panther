package com.re.paas.internal.infra.filesystem;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import com.re.paas.api.infra.filesystem.NativeFileSystem;

public class NativeFileSystemImpl implements NativeFileSystem {

	@Override
	public FileSystem getInternal() {
		return FileSystemProviders.getInternal();
	}

	@Override
	public Path getResourcePath() {
		return FileSystemProviders.getResourcePath();
	}
}
