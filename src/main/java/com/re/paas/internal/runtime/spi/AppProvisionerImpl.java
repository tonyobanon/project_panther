package com.re.paas.internal.runtime.spi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.fusion.server.JsonArray;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.SpiBase;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.errors.SpiError;

@BlockerTodo("Create ThreadGroup(s) for each application, so that ThreadSecurity#newThread can use it")
public class AppProvisionerImpl implements AppProvisioner {

	private static Logger LOG = Logger.get(AppProvisionerImpl.class);

	public static final String APP_BUNDLES_COLLECTION = "app_bundles";

	public static Map<String, AppClassLoader> appClassloaders = Collections
			.synchronizedMap(new HashMap<String, AppClassLoader>());

	static Map<String, JsonObject> appConfig = Collections.synchronizedMap(new HashMap<String, JsonObject>());

	private static final Path basePath = Platform.getResourcePath().resolve("apps");

	public AppProvisionerImpl() {
		if (!Files.exists(basePath)) {
			try {
				Files.createDirectories(basePath);
			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}
		}
	}

	static Path getAppBasePath() {
		return basePath;
	}

	public Boolean install(Path archive) {

		// Save to DB, along with version

		Table col = Database.get().getTable(APP_BUNDLES_COLLECTION);

		// Extract to app directory

		// Ensure that all dependencies are available

		Path path = null; // directory that now has app artifact, sequel to extraction
		// Remove to use internal filesystem

		String appId = path.getFileName().toString();

		readConfiguration(path);

		SpiBaseImpl.install(getClassloader(appId));

		return true;
	}

	public void list() {

	}

	private static String[] getAppDependencies(String appId) {

		JsonArray arr = AppProvisionerImpl.appConfig.get(appId).getJsonArray("app_dependencies");

		if (arr != null) {
			String[] array = new String[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				array[i] = arr.getString(i);
			}
			return array;
		} else {
			return new String[] {};
		}
	}

	public static String getAppId() {
		AppClassLoader cl = (AppClassLoader) Thread.currentThread().getContextClassLoader();
		return cl != null ? cl.getAppId() : DEFAULT_APP_ID;
	}

	public static String getAppName() {
		return AppProvisioner.get().getAppName(getAppId());
	}

	public void start() {
		try {
			start0();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	private static void readConfiguration(Path path) {

		String appId = path.getFileName().toString();

		JsonObject config = null;
		try {
			config = Utils.getJson(Files.newInputStream(path.resolve("config.json")));
		} catch (Exception e) {
			LOG.warn("Could not find config.json for app: " + appId);
			config = new JsonObject();
		}

		appConfig.put(appId, config);

		// Create classloader
		AppClassLoader cl = AppClassLoader.get(appId, getAppDependencies(appId));

		// Save classloader
		appClassloaders.put(appId, cl);
	}

	@BlockerBlockerTodo("Sort that date. Therefore least recent date comes in first, Attend to comments")
	private static void start0() throws IOException {

		// Get bundles from DB,

		// And extract them to appBase

		Path appBase = getAppBasePath();

		if (!Files.exists(appBase)) {
			Files.createDirectories(appBase);
		}

		Stream<Path> paths = Files.list(appBase);

		paths.forEach(path -> {

			if (!Files.isDirectory(path)) {
				return;
			}

			LOG.debug("Loading app: " + path.getFileName());
			readConfiguration(path);
		});

		LOG.debug("All applications loaded in " + AppProvisioner.class.getSimpleName());
		paths.close();
	}

	@BlockerTodo("Schedule Platform Restart ?")
	public void stop(String app) {

		assert !app.equals(DEFAULT_APP_ID);

		AppClassLoader cl = appClassloaders.get(app);

		@SuppressWarnings("unused")
		JsonObject obj = appConfig.get(app);

		if (!SpiBase.get().stop(app)) {
			Exceptions.throwRuntime(PlatformException.get(SpiError.APPLICATION_IS_CURRENTLY_IN_USE, app));
		}

		cl = null;
		obj = null;

		appClassloaders.remove(app);
		appConfig.remove(app);
	}

	public Set<String> listApps() {
		return appClassloaders.keySet();
	}

	static AppClassLoader getAppClassloader(String appId) {
		return appClassloaders.get(appId);
	}

	public AppClassLoader getClassloader(String appId) {
		return getAppClassloader(appId);
	}

	public JsonObject getConfig(String appId) {
		return appConfig.get(appId);
	}

	public String getAppName(String appId) {
		return getConfig(appId).getString("name");
	}

}
