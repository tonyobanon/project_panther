package com.re.paas.internal.runtime.spi;

import java.nio.file.Path;

import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;

public class Classpaths {

	@SecureMethod
	public static Path get(ClassLoader cl) {
		return cl != null && cl instanceof AppClassLoader ? ((AppClassLoader) cl).getPath()
				: FileSystemProviders.getInternal().getPath(ClassLoaders.getClassLoader().getResource(".").getPath());
	}

	@SecureMethod
	public static Path get() {
		return get(ClassLoader.getSystemClassLoader());
	}

}
