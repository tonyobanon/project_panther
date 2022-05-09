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
import com.re.paas.api.annotations.develop.Note;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.infra.filesystem.NativeFileSystem;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.runtime.spi.AppProvisioner;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.errors.SpiError;
import com.re.paas.internal.runtime.RuntimeIdentityImpl;

@BlockerTodo("Create ThreadGroup(s) for each application, so that ThreadSecurity#newThread can use it")
public class AppProvisionerImpl implements AppProvisioner {

	private static Logger LOG = Logger.get(AppProvisionerImpl.class);

	static Map<String, AppClassLoader> appClassloaders = Collections
			.synchronizedMap(new HashMap<String, AppClassLoader>());

	static Map<String, JsonObject> appConfig = Collections.synchronizedMap(new HashMap<String, JsonObject>());

	private static final Path basePath = NativeFileSystem.get().getResourcePath().resolve("apps");

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

	@BlockerTodo("On installation, we want to scan through the classes, and ensure that it is not found in an existing classloader")
	@Note("Don't we want to eargerly load all classes to make the above even possible")
	public void install(Path archive) {

		// Extract to app directory

		// Ensure that all dependencies are available

		Path path = null; // directory that now has app artifact, sequel to extraction
		// Remove to use internal filesystem

		String appId = path.getFileName().toString();
		AppClassLoader cl = getClassloader(appId);

		readConfiguration(path);

		SpiDelegateHandlerImpl.registerDelegates(cl);

		SpiBaseImpl.start(appId);
	}

	@Override
	public void uninstall(String appId) {

		AppClassLoader cl = getClassloader(appId);

		SpiDelegateHandlerImpl.unregisterDelegates(cl);

		stop0(appId, true);
	}

	public static String getAppId() {
		AppClassLoader cl = (AppClassLoader) Thread.currentThread().getContextClassLoader();
		return cl != null ? cl.getAppId() : DEFAULT_APP_ID;
	}

	public static String getAppName() {
		return AppProvisioner.get().getAppName(getAppId());
	}

	@Override
	public void scanApps() {
		try {
			scan0();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	/**
	 * This loads the RuntimeIdentity into the specified classloader
	 * 
	 * @param cl
	 */
	private static void load(AppClassLoader cl) {

		try {

			cl.loadClass(ClassUtils.getName(RuntimeIdentity.class));

			@SuppressWarnings("unchecked")
			Class<RuntimeIdentityImpl> tscImpl = (Class<RuntimeIdentityImpl>) cl
					.loadClass(ClassUtils.getName(RuntimeIdentityImpl.class));

			tscImpl.getDeclaredMethod("noOp").invoke(null);

		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
				| java.lang.reflect.InvocationTargetException | NoSuchMethodException | SecurityException e) {
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
		AppClassLoader cl = new AppClassLoaderImpl(ClassLoader.getSystemClassLoader(), appId);

		// Load RuntimeIdentity
		load(cl);

		// Save classloader
		appClassloaders.put(appId, cl);
	}

	@BlockerBlockerTodo("Sort that date. Therefore least recent date comes in first, Attend to comments")
	private static void scan0() throws IOException {

		Path appBase = getAppBasePath();

		if (!Files.exists(appBase)) {
			Files.createDirectories(appBase);
		}

		Stream<Path> paths = Files.list(appBase);

		paths.forEach(path -> {

			if (!Files.isDirectory(path)) {
				return;
			}

			LOG.debug("Scanning app: " + path.getFileName());
			readConfiguration(path);
		});

		LOG.debug("All applications loaded in " + AppProvisioner.class.getSimpleName());
		paths.close();
	}

	@BlockerTodo("Schedule Platform Restart ?")
	private void stop0(String app, boolean forUninstall) {

		assert !app.equals(DEFAULT_APP_ID);

		AppClassLoader cl = appClassloaders.get(app);

		@SuppressWarnings("unused")
		JsonObject obj = appConfig.get(app);

		if (!SpiBaseImpl.stop(app, forUninstall)) {
			Exceptions.throwRuntime(PlatformException.get(SpiError.APPLICATION_IS_CURRENTLY_IN_USE, app));
		}

		appClassloaders.remove(app);
		appConfig.remove(app);
	}

	public Set<String> listApps() {
		return listApps0();
	}

	static Set<String> listApps0() {
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
