package com.re.paas.api.infra.filesystem;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.SecureMethod;

public interface NativeFileSystem {
	
	public static NativeFileSystem get() {
		return Singleton.get(NativeFileSystem.class);
	}

	@SecureMethod
	public FileSystem getInternal();

	@SecureMethod
	public Path getResourcePath();
}
