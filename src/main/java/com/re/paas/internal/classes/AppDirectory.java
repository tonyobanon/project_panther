package com.re.paas.internal.classes;

import java.io.InputStream;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;
import com.re.paas.internal.runtime.security.Secure;

/**
 * This class provides static helper methods to read config files, as well as
 * scan the classpath. This class should not be annotated with {@link Secure}
 * as it cannot be re-injected into the base class loader without any error
 * 
 * @author Tony
 */
public class AppDirectory {

	private static JsonObject config;

	public static Path getBasePath() {
		return FileSystemProviders.getInternal().getPath(getBaseClassloader().getResource("").getPath());
	}

	public static ClassLoader getBaseClassloader() {
		return ClassLoader.getSystemClassLoader();
	}

	public static InputStream getInputStream(String resource) {
		return getBaseClassloader().getResourceAsStream(resource);
	}

	public static JsonObject getConfig() {
		return config;
	}

	static {

		try {
			config = Utils.getJson(AppDirectory.getInputStream("config.json"));
		} catch (Exception e) {
			throw new RuntimeException("Error occured while loading config.json", e);
		}
	}

}
