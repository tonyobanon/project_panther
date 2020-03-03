package com.re.paas.internal;

import java.nio.file.Files;
import java.nio.file.Path;

import com.re.paas.api.Activator;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;

public class ActivatorImpl implements Activator {

	private static Boolean isInstalled = null;

	public Boolean isInstalled() {

		if (isInstalled == null) {
			Path p = FileSystemProviders.getResourcePath().resolve(".installed");
			isInstalled = Files.exists(p);
		}

		return isInstalled;
	}

}
