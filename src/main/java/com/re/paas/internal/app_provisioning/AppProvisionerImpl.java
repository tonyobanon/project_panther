package com.re.paas.internal.app_provisioning;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.spi.SpiBase;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.classes.AppDirectory;
import com.re.paas.internal.errors.SpiError;

public class AppProvisionerImpl implements AppProvisioner {

	private static final String APP_DEPENDENCIES_CONFIG_KEY = "app_dependencies";

	public static final String DEFAULT_APP_ID = "default";

	private static Map<String, AppClassLoader> appClassloaders = Collections
			.synchronizedMap(new HashMap<String, AppClassLoader>());

	static Map<String, JsonObject> appConfig = Collections.synchronizedMap(new HashMap<String, JsonObject>());

	private static final Path BASE_APPS_PATH;

	private static Path getAppBasePath() {
		return BASE_APPS_PATH;
	}
	
	@Override
	public String defaultAppId() {
		return DEFAULT_APP_ID;
	}

	public void install(String uri) {

		// Fetch bundle

		// Save to DB, along with version

		// Extract to app directory

		// Get app Id
		String app = null;

		SpiBase.get().start(app);
	}

	public void list() {

	}

	private static String[] getAppDependencies(String appId) {

		JsonElement v = AppProvisionerImpl.appConfig.get(appId).get(APP_DEPENDENCIES_CONFIG_KEY);

		JsonArray arr = v != null ? v.getAsJsonArray(): null;

		if (arr != null) {
			String[] array = new String[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				array[i] = arr.get(i).toString();
			}
			return array;
		} else {
			return new String[] {};
		}
	}

	public void start() {
		try {
			start0();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	@BlockerBlockerTodo("Sort that date. Therefore least recent date comes in first, Attend to comments")
	private static void start0() throws IOException {

		Path appBase = getAppBasePath();

		if (!Files.exists(appBase)) {
			Files.createDirectories(appBase);
		}

		// Get bundles from DB,

		// And extract them to appBase

		Stream<Path> paths = Files.list(appBase);
		
		System.out.println(paths.count());

		// Call getAppDependencies(..) and ensure that all dependencies are available

		paths.forEach(path -> {

			String appId = path.getFileName().toString();

			JsonObject config = null;
			try {
				config = Utils.getJson(Files.newInputStream(path.resolve("config.json")));
			} catch (Exception e) {
				Logger.get(AppProvisionerImpl.class).warn("Could not config.json for app: " + appId);
				config = new JsonObject();
			}

			appConfig.put(appId, config);

			// Create classloader
			AppClassLoader cl = AppClassLoader.get(path, appId, getAppDependencies(appId));

			appClassloaders.put(appId, cl);
		});

		paths.close();
	}

	public void stop(String app) {

		assert !app.equals(DEFAULT_APP_ID);

		AppClassLoader cl = appClassloaders.get(app);

		@SuppressWarnings("unused")
		JsonObject obj = appConfig.get(app);

		cl.setStopping(true);

		if (!SpiBase.get().canStop(app)) {

			cl.setStopping(false);

			Exceptions.throwRuntime(PlatformException.get(SpiError.APPLICATION_IS_CURRENTLY_IN_USE, app));
		}

		SpiBase.get().stop(app);

		cl = null;
		obj = null;

		appClassloaders.remove(app);
		appConfig.remove(app);

		// Todo: Schedule Application Restart
	}

	public Collection<String> listApps() {
		return appClassloaders.keySet();
	}

	public AppClassLoader getClassloader(String appId) {
		return appClassloaders.get(appId);
	}

	public String getAppId(Class<?> clazz) {
		ClassLoader cl = clazz.getClassLoader();

		if (cl instanceof AppClassLoader) {
			return ((AppClassLoader) cl).getAppId();
		} else {
			return DEFAULT_APP_ID;
		}
	}

	public JsonObject getConfig(String appId) {
		return appConfig.get(appId);
	}

	private Map<JsonObject, ClassLoader> getConfigurations(String appId) {

		LinkedHashMap<JsonObject, ClassLoader> m = new LinkedHashMap<>();

		m.put(AppDirectory.getConfig(), AppProvisionerImpl.class.getClassLoader());

		if (appId.equals(DEFAULT_APP_ID)) {

			for (String x : listApps()) {
			
				AppClassLoader cl = getClassloader(x);

				if (!cl.isStopping()) {
					m.put(getConfig(x), cl);
				}
			}

		} else {
			AppClassLoader cl = getClassloader(appId);

			if (!cl.isStopping()) {
				m.put(getConfig(appId), cl);
			}
		}
		
		return m;
	}

	public KeyValuePair<String, ClassLoader> getConfiguration(String appId, String key) {

		Map<JsonObject, ClassLoader> configuration = getConfigurations(appId);

		KeyValuePair<String, ClassLoader> r = null;

		for (Entry<JsonObject, ClassLoader> e : configuration.entrySet()) {

			JsonObject config = e.getKey();
			ClassLoader cl = e.getValue();

			JsonElement v = config.get(key);

			if (v != null) {
				r = new KeyValuePair<String, ClassLoader>(v.toString(), cl);
			}
		}
		return r;
	}

	static {
		
		// Set up base path for applications
		BASE_APPS_PATH = Platform.getResourcePath().resolve("apps");
		
		if(!Files.exists(BASE_APPS_PATH)) {
			try {
				Files.createDirectory(BASE_APPS_PATH);
			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}
		}
	}
}
