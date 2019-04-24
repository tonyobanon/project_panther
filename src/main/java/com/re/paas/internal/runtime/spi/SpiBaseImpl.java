package com.re.paas.internal.runtime.spi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ExecutorFactoryConfig;
import com.re.paas.api.runtime.spi.SpiBase;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.runtime.spi.TypeClassification;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;

public class SpiBaseImpl implements SpiBase {

	private static final Path basePath = Platform.getResourcePath().resolve("spi_config");

	private static final Path defaultDelegatesPath = basePath.resolve("default_delegates.json");
	private static final Path availableDelegatesPath = basePath.resolve("available_delegates.json");
	private static final Path trustedAppsPath = basePath.resolve("trusted_apps.json");

	private static JsonObject defaultDelegatesConfig;
	private static JsonObject availableDelegatesConfig;
	private static JsonArray trustedAppsConfig;

	public SpiBaseImpl() {

		createResourceFiles();

		availableDelegatesConfig = Utils.getJson(availableDelegatesPath);
		defaultDelegatesConfig = Utils.getJson(defaultDelegatesPath);
		trustedAppsConfig = Utils.getJsonArray(trustedAppsPath);
	}

	private static void createResourceFiles() {

		try {

			if (!Files.exists(basePath)) {
				Files.createDirectories(basePath);
			}

			if (!Files.exists(defaultDelegatesPath)) {
				Files.createFile(defaultDelegatesPath);
				Utils.saveString("{}", defaultDelegatesPath);
			}

			if (!Files.exists(availableDelegatesPath)) {
				Files.createFile(availableDelegatesPath);

				JsonObject contents = new JsonObject();

				for (SpiType type : SpiType.values()) {
					contents.add(type.toString(), new JsonArray());
				}

				Utils.saveString(contents.toString(), availableDelegatesPath);
			}

			if (!Files.exists(trustedAppsPath)) {
				Files.createFile(trustedAppsPath);
				Utils.saveString("[]", trustedAppsPath);
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

	}

	static boolean isTrusted(String appId) {
		return trustedAppsConfig.contains(new JsonPrimitive(appId));
	}

	static void registerTrustedApp(String appId) {

		if (!isTrusted(appId)) {

			trustedAppsConfig.add(appId);
			Utils.saveString(trustedAppsConfig.toString(), trustedAppsPath);
		}
	}

	static Class<? extends SpiDelegate<?>> getDefaultDelegate(SpiType type) {

		String key = type.toString();

		String value = defaultDelegatesConfig.has(key) ? defaultDelegatesConfig.get(key).getAsString() : null;

		return value != null ? ClassUtils.forName(value) : null;
	}

	static synchronized void registerDefaultDelegate(SpiType type, Class<? extends SpiDelegate<?>> delegateClass) {

		if (type.classification() == TypeClassification.ACTIVE && !isTrusted(ClassUtils.getAppId(delegateClass))) {
			Exceptions.throwRuntime("Type: " + type.toString() + " requires a trusted delegate");
		}

		String key = type.toString();

		String value = defaultDelegatesConfig.has(key) ? defaultDelegatesConfig.get(key).getAsString() : null;

		if (value != null) {

			if (value.equals(ClassUtils.toString(delegateClass))) {
				// Entry already exists
				return;
			} else {
				// Remove existing
				defaultDelegatesConfig.remove(key);
			}
		}

		defaultDelegatesConfig.addProperty(key, ClassUtils.toString(delegateClass));

		Utils.saveString(defaultDelegatesConfig.toString(), defaultDelegatesPath);
	}

	/**
	 * This returns the delegate classes as strings
	 */
	static List<Class<? extends SpiDelegate<?>>> getAvailableDelegates(SpiType type) {

		JsonArray array = (JsonArray) availableDelegatesConfig.get(type.toString());
		List<Class<? extends SpiDelegate<?>>> delegateClasses = new ArrayList<>(array.size());

		array.forEach(e -> {
			String className = e.getAsString();
			delegateClasses.add(ClassUtils.createInstance(className));
		});

		return delegateClasses;
	}

	static void registerAvailableDelegates(SpiType type, Class<? extends SpiDelegate<?>> delegateClass) {

		JsonArray array = (JsonArray) availableDelegatesConfig.get(type.toString());
		String className = ClassUtils.toString(delegateClass);

		if (!array.contains(new JsonPrimitive(className))) {
			array.add(className);
		}

		Utils.saveString(availableDelegatesConfig.toString(), availableDelegatesPath);
	}

	@BlockerBlockerTodo("See comments")
	public void start(String appId) {
		
		ExecutorFactory.create("default", new ExecutorFactoryConfig(ExecutorFactory.MAX_THREAD_COUNT));

		if (appId.equals(AppProvisioner.DEFAULT_APP_ID)) {

			// The entire platform is starting..
			
			// Call Marketplace to validate apps for this installation

			start0(appId);

			// Start apps
			AppProvisioner.get().listApps().forEach(SpiBaseImpl::start0);

		} else {
			start0(appId);
		}
	}

	private static void start0(String appId) {

		SPILocatorHandlerImpl.start(appId, SpiType.values());

		SpiDelegateHandlerImpl.start(null, appId, SpiType.values());

		if (!appId.equals(AppProvisioner.DEFAULT_APP_ID)) {
			BaseEvent.dispatch(new AppStartEvent(appId, true));
		}

		SPILocatorHandlerImpl.reshuffleClasses();
	}

	public void stop() {

		Map<SpiType, SpiDelegate<?>> delegatesMap = SpiDelegateHandler.get().getDelegates();

		Iterator<SpiDelegate<?>> it = delegatesMap.values().iterator();
		int len = delegatesMap.size();

		ArrayList<SpiDelegate<?>> delegates = new ArrayList<>(len);

		// We need to invert the map, then call destroy
		for (int i = len - 1; i >= 0; i--) {
			delegates.add(i, it.next());
		}

		delegates.forEach(delegate -> {
			delegate.shutdown();
			
			Map<Object, Object> rMap = SpiDelegateHandler.get().getResources(delegate.getSpiType());
			
			if(!rMap.isEmpty()) {
				rMap.clear();
			}
			
		});
	}

	public Boolean stop(String appId) {

		if (!canStop(appId)) {
			return false;
		}

		AppClassLoader cl = AppProvisioner.get().getClassloader(appId);
		cl.setStopping(true);

		Logger.get().info("Stopping application: " + appId);
		return stop0(appId);
	}

	/**
	 * Behind the scenes, this method checks if this app has an Spi delegate/locator
	 * that can be taken out of service and replaced with a similar delegate from
	 * another app
	 * 
	 * @param appId
	 * @return
	 */
	@PlatformInternal
	public boolean canStop(String appId) {

		AppClassLoader cl = AppProvisioner.get().getClassloader(appId);
		Map<SpiType, SpiDelegate<?>> delegates = SpiDelegateHandler.get().getDelegates();

		for (SpiType type : SpiType.values()) {

			if (delegates.get(type).getClass().getClassLoader().equals(cl)) {

				// Remove from defaults

				// Call findDelegate()

			}
		}

		return true;
	}

	private static Boolean stop0(String appId) {

		Map<SpiType, SpiDelegate<?>> delegates = SpiDelegateHandler.get().getDelegates();
		AppClassLoader cl = AppProvisioner.get().getClassloader(appId);

		for (Entry<SpiType, Map<String, List<Class<?>>>> e : SPILocatorHandlerImpl.getSpiClasses().entrySet()) {

			SpiType type = e.getKey();
			Map<String, List<Class<?>>> allClasses = e.getValue();

			assert cl.isStopping();

			List<Class<?>> classes = allClasses.remove(appId);

			if (classes == null || classes.isEmpty()) {
				continue;
			}

			SpiDelegate<?> delegate = delegates.get(type);

			List<Class<?>> usedClasses = Utils.fromGenericList(delegate.remove0(classes));

			if (!usedClasses.isEmpty()) {

				BaseEvent.dispatch(new AppStopEvent(appId, false).setReason(usedClasses));
				return false;
			}
		}

		SPILocatorHandlerImpl.getSpiClasses().forEach((type, allClasses) -> {

			if (SPILocatorHandlerImpl.getDefaultLocators().get(type).getClass().getClassLoader().equals(cl)) {

				// Start new locator
				SPILocatorHandlerImpl.start(null, new SpiType[] { type });
			}

			SpiDelegate<?> delegate = delegates.get(type);

			if (delegate.getClass().getClassLoader().equals(cl)) {
				// Start new delegate
				SpiDelegateHandlerImpl.start(null, null, new SpiType[] { type });
			}
		});

		// Remove as trusted

		BaseEvent.dispatch(new AppStopEvent(appId, true));
		return true;
	}

	static String getLocatorConfigKey(SpiType type) {
		return "platform.spi." + type.toString().toLowerCase() + ".locator";
	}

	static String getDelegateConfigKey(SpiType type) {
		return "platform.spi." + type.toString().toLowerCase() + ".delegate";
	}

}
