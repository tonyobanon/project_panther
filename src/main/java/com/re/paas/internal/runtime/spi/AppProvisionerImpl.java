package com.re.paas.internal.runtime.spi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ThreadSecurity;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.runtime.spi.SpiBase;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.runtime.spi.TypeClassification;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.errors.SpiError;
import com.re.paas.internal.fusion.ui.impl.UIContext;

@BlockerTodo("Create ThreadGroup(s) for each application, so that ThreadSecurity#newThread can use it")
public class AppProvisionerImpl implements AppProvisioner {

	private static Logger LOG = Logger.get(AppProvisionerImpl.class);

	public static final String APP_BUNDLES_COLLECTION = "app_bundles";

	private static Map<String, AppClassLoader> appClassloaders = Collections
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

	private static Path getAppBasePath() {
		return basePath;
	}

	public boolean install(Path archive) {

		// Save to DB, along with version

		Table col = Database.get().getTable(APP_BUNDLES_COLLECTION);

		// Extract to app directory

		// Ensure that all dependencies are available

		Path path = null; // directory that now has app artifact, sequel to extraction

		String appId = path.getFileName().toString();

		readConfiguration(path);

		return scanAvailableDelegates(appId, path);
	}

	public void list() {

	}

	private static String[] getAppDependencies(String appId) {

		JsonElement v = AppProvisionerImpl.appConfig.get(appId).get("app_dependencies");

		JsonArray arr = v != null ? v.getAsJsonArray() : null;

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

	/**
	 * 
	 * @param appId
	 * @param path
	 * @return A boolean indicating whether the application is required to start
	 *         immediately
	 */
	private boolean scanAvailableDelegates(String appId, Path path) {

		boolean hasActive = false;

		for (SpiType type : SpiType.values()) {

			BaseSPILocator locator = SPILocatorHandlerImpl.getDefaultLocators().get(type);

			@SuppressWarnings("unchecked")
			Class<SpiDelegate<?>> delegateType = (Class<SpiDelegate<?>>) locator.delegateType();

			AppClassLoader cl = getClassloader(appId);

			List<Class<? extends SpiDelegate<?>>> delegateClasses = new ClasspathScanner<>(delegateType,
					ClassIdentityType.ASSIGNABLE_FROM).setClassLoader(cl).scanClasses();

			if (delegateClasses.isEmpty()) {
				continue;
			}

			String confirmMessage = ClientRBRef.get("app.confirm.set.default.delegate").add(type + ".action")
					.toString();

			boolean useAsDefault = UIContext.confirm(confirmMessage);

			if (useAsDefault && type.classification() == TypeClassification.ACTIVE) {

				String confirmTrust = ClientRBRef.get("app.confirm.trust.set.default.delegate").toString();
				useAsDefault = UIContext.confirm(confirmTrust);

				if (useAsDefault) {
					SpiBaseImpl.registerTrustedApp(appId);
					hasActive = true;
				}
			}

			if (useAsDefault) {
				SpiBaseImpl.registerDefaultDelegate(type, delegateClasses.get(0));
			} else {
				SpiBaseImpl.registerAvailableDelegates(type, delegateClasses.get(0));
			}

		}

		return hasActive;
	}

	private static void readConfiguration(Path path) {

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
		AppClassLoader cl = AppClassLoader.get(path.resolve("classes"), appId, getAppDependencies(appId));

		// Load ThreadSecurity class into the classloader
		ThreadSecurity.load(cl);

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

		System.out.println(paths.count());

		paths.forEach(path -> {
			readConfiguration(path);
		});

		paths.close();
	}

	@BlockerTodo("Schedule Application Restart")
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

	public AppClassLoader getClassloader(String appId) {
		return appClassloaders.get(appId);
	}

	public JsonObject getConfig(String appId) {
		return appConfig.get(appId);
	}
}
