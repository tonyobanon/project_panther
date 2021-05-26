package com.re.paas.internal;

import java.nio.file.Files;
import java.nio.file.Path;

import com.re.paas.api.Activator;
import com.re.paas.api.infra.filesystem.NativeFileSystem;

public class ActivatorImpl implements Activator {

	private static Boolean isInstalled = null;

	public Boolean isInstalled() {

		if (isInstalled == null) {
			Path p = NativeFileSystem.get().getResourcePath().resolve(".installed");
			isInstalled = Files.exists(p);
		}

		return isInstalled;
	}
	
	@Override
	public void restartNeeded() {
		// Todo
	}
}
