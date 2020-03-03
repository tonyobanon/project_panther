package com.re.paas.api.runtime;

import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.runtime.spi.AppProvisioner;
import com.re.paas.api.utils.Utils;

public class ClassLoaders {

	private static final String configFile = "config.json";
	private static JsonObject config = getConfiguration(getClassLoader());

	@SecureMethod
	public static ClassLoader getClassLoader() {
		return getClassLoader(null);
	}

	@SecureMethod
	public static ClassLoader getClassLoader(String id) {
		return id == null || id.equals(AppProvisioner.DEFAULT_APP_ID) ? ClassLoader.getSystemClassLoader()
				: AppProvisioner.get().getClassloader(id);
	}

	public static String getId(ClassLoader cl) {
		return cl != null && cl instanceof AppClassLoader ? ((AppClassLoader) cl).getAppId()
				: AppProvisioner.DEFAULT_APP_ID;
	}
	
	public static String getId(Class<?> c) {
		return getId(c.getClassLoader());
	}

	@SecureMethod
	public static JsonObject getConfiguration() {
		return config;
	}

	@SecureMethod
	public static JsonObject getConfiguration(ClassLoader cl) {
		return cl != null && cl instanceof AppClassLoader
				? AppProvisioner.get().getConfig(((AppClassLoader) cl).getAppId())
				: Utils.getJson(getClassLoader().getResourceAsStream(configFile));
	}
}
