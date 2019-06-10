package com.re.paas.internal.runtime.spi;

import java.nio.file.Path;

import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.infra.filesystem.FileSystemProviders;

public class ClassLoaders {

	private static final String configFile = "config.json";
	private static JsonObject config = getConfiguration(getClassLoader());

	@MethodMeta
	public static ClassLoader getClassLoader() {
		return getClassLoader(null);
	}

	@MethodMeta
	public static ClassLoader getClassLoader(String id) {
		return id == null || id.equals(AppProvisioner.DEFAULT_APP_ID) ? ClassLoader.getSystemClassLoader()
				: AppProvisioner.get().getClassloader(id);
	}

	@MethodMeta
	public static Path getClassPath() {
		return getClassPath(ClassLoader.getSystemClassLoader());
	}

	@MethodMeta
	public static Path getClassPath(ClassLoader cl) {
		return cl != null && cl instanceof AppClassLoader ? ((AppClassLoader) cl).getPath()
				: FileSystemProviders.getInternal().getPath(getClassLoader().getResource(".").getPath());
	}

	@MethodMeta
	public static String getId(ClassLoader cl) {
		return cl != null && cl instanceof AppClassLoader ? ((AppClassLoader) cl).getAppId()
				: AppProvisioner.DEFAULT_APP_ID;
	}

	@MethodMeta
	public static JsonObject getConfiguration() {
		return config;
	}

	@MethodMeta
	public static JsonObject getConfiguration(ClassLoader cl) {
		return cl != null && cl instanceof AppClassLoader
				? AppProvisioner.get().getConfig(((AppClassLoader) cl).getAppId())
				: Utils.getJson(getClassLoader().getResourceAsStream(configFile));
	}
}
