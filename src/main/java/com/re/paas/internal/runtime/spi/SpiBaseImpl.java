package com.re.paas.internal.runtime.spi;

import static com.re.paas.internal.runtime.spi.SpiDelegateConfigHandler.Tier.PLATFORM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.fusion.server.JsonArray;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiBase;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;

public class SpiBaseImpl implements SpiBase {

	private static final Logger LOG = Logger.get(SpiBaseImpl.class);

	static final Path spiConfigBasePath = Platform.getResourcePath().resolve("spi_config");

	private static final Path trustedAppsPath = SpiBaseImpl.spiConfigBasePath.resolve("trusted_apps.json");
	private static JsonArray trustedAppsConfig;

	public SpiBaseImpl() {

		createResourceFiles();

		trustedAppsConfig = Utils.getJsonArray(trustedAppsPath);
	}

	private static void createResourceFiles() {

		try {
			if (!Files.exists(trustedAppsPath)) {
				Files.createFile(trustedAppsPath);
				Utils.saveString("[]", trustedAppsPath);
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

	}

	static boolean isAppTrusted(String appId) {
		return trustedAppsConfig.contains(appId);
	}

	static void registerTrustedApp(String appId) {

		if (!isAppTrusted(appId)) {

			trustedAppsConfig.add(appId);
			Utils.saveString(trustedAppsConfig.toString(), trustedAppsPath);
		}
	}

	public void start(Collection<String> apps) {

		LOG.debug("Discovering classes ..");

		ClassLoader cl = ClassLoaders.getClassLoader();
		SPILocatorHandlerImpl.start(cl, SpiType.values());

		apps.forEach(appId -> {
			SPILocatorHandlerImpl.start(ClassLoaders.getClassLoader(appId), SpiType.values());
		});

		LOG.debug("Transferring to delegates ..");

		SpiDelegateHandlerImpl.start(cl, SpiType.values(), null);

		BaseEvent.dispatch(new AppStartEvent(ClassLoaders.getId(cl), true));
	}

	public void start(String appId) {

		LOG.debug("Discovering classes ..");

		ClassLoader cl = ClassLoaders.getClassLoader(appId);
		SPILocatorHandlerImpl.start(cl, SpiType.values());

		LOG.debug("Transferring to delegates ..");

		SpiDelegateHandlerImpl.start(cl, SpiType.values(), null);

		BaseEvent.dispatch(new AppStartEvent(appId, true));
	}

	public void stop() {

		Map<SpiType, SpiDelegate<?>> delegatesMap = SpiDelegateHandler.get().getDelegates();

		Iterator<SpiDelegate<?>> it = delegatesMap.values().iterator();
		int len = delegatesMap.size();

		ArrayList<SpiDelegate<?>> delegates = new ArrayList<>(len);

		// We need to invert the map
		for (int i = len - 1; i >= 0; i--) {
			delegates.add(i, it.next());
		}

		delegates.forEach(delegate -> {

			// Indicate that delegate needs to be taken out of service
			delegate.shutdown();

			// Release resources used by this delegate
			delegate.release();
		});
	}

	public Boolean stop(String appId) {

		AppClassLoader cl = AppProvisioner.get().getClassloader(appId);
		cl.setStopping(true);

		stop(cl);
		return true;
	}

	@BlockerTodo("Process result of delegate.init()")
	static void stop(ClassLoader cl) {

		String appId = ClassLoaders.getId(cl);

		// Remove locator type suffixes
		BaseSPILocator.removeTypeSuffixes(SpiType.values(), appId);

		for (SpiType type : SpiType.values()) {

			SpiDelegate<?> delegate = SpiDelegateHandlerImpl.delegates.get(type);

			// Remove app classes from locator map
			List<Class<?>> classes = SPILocatorHandlerImpl.getSpiClasses().get(type).remove(appId);

			// Unload app classes from current delegate
			delegate.remove0(classes);

			if (ClassUtils.getAppId(delegate.getClass()).equals(appId)) {

				// Indicate that delegate needs to be taken out of service
				delegate.shutdown();

				// Release resources used by this delegate
				delegate.release();

				// If current delegate originates from app, use platform's version for now
				delegate = ClassUtils.createInstance(SpiDelegateConfigHandler.get(type, PLATFORM));

				delegate.init();

				SpiDelegateHandlerImpl.delegates.put(type, delegate);
			}
		}

		// Remove entries for <appId> in the resource validator cache
		SpiDelegate.emptyResourceValidatorCache(appId);
	}

	static void install(ClassLoader cl) {
		SpiDelegateHandlerImpl.install(cl);
	}

	static void uninstall(ClassLoader cl) {
		SpiDelegateHandlerImpl.uninstall(cl);
	}

}
