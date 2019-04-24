package com.re.paas.internal.runtime.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.runtime.spi.SpiLocatorHandler;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.AppDirectory;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.errors.ApplicationError;

public class SPILocatorHandlerImpl implements SpiLocatorHandler {

	private static final Logger LOG = Logger.get(BaseSPILocator.class);

	static Map<SpiType, BaseSPILocator> defaultSpiLocator = Collections
			.synchronizedMap(new HashMap<SpiType, BaseSPILocator>());

	static Map<SpiType, Map<String, List<Class<?>>>> spiClasses = Collections
			.synchronizedMap(new HashMap<SpiType, Map<String, List<Class<?>>>>());

	/**
	 * After a call to {@link SpiDelegateHandlerImpl#start} has been made in SPIBase, we now know
	 * the dependency hierarchy of the delegates, so we therefore have to re-arrange
	 * the entries in the spiClasses map
	 */
	static void reshuffleClasses() {

		Map<SpiType, Map<String, List<Class<?>>>> spiClasses = Collections
				.synchronizedMap(new LinkedHashMap<SpiType, Map<String, List<Class<?>>>>());

		Map<SpiType, SpiDelegate<?>> delegatesMap = SpiDelegateHandler.get().getDelegates();

		SpiType[] types = new SpiType[delegatesMap.size()];
		Integer i = types.length - 1;

		for (SpiType type : delegatesMap.keySet()) {
			types[i] = type;
			i--;
		}

		assert i.equals(-1);

		for (SpiType type : types) {
			spiClasses.put(type, SPILocatorHandlerImpl.spiClasses.get(type));
		}

		synchronized (SPILocatorHandlerImpl.spiClasses) {
			SPILocatorHandlerImpl.spiClasses = spiClasses;
		}
	}

	private static BaseSPILocator findLocator(SpiType type) {
		String key = SpiBaseImpl.getLocatorConfigKey(type);
		return ClassUtils.createInstance(AppDirectory.getConfig().get(key).getAsString());
	}

	static void start(String appId, SpiType[] types) {

		for (SpiType type : types) {

			BaseSPILocator locator = defaultSpiLocator.get(type);

			boolean isNew = locator == null;

			assert isNew == appId.equals(AppProvisioner.DEFAULT_APP_ID);

			if (isNew) {

				locator = findLocator(type);

				LOG.info("Starting new SPI Locator, app = " + appId + " , type = " + type.toString());

				// Verify that declared spi type corresponds to type
				if (!(locator.spiType() == type)) {
					Exceptions.throwRuntime(
							PlatformException.get(ApplicationError.SERVICE_PROVIDER_CLASS_NOT_CONCRETE_IMPL,
									ClassUtils.toString(locator.getClass())));
				}

				// Verify that no other spi locator uses classType
				for (Entry<SpiType, BaseSPILocator> e : SPILocatorHandlerImpl.defaultSpiLocator.entrySet()) {
					if (locator.classType().isAssignableFrom(e.getValue().classType())) {
						Exceptions.throwRuntime(
								"Type: " + locator.classType().getName() + " is already defined for SpiType: "
										+ e.getKey() + " --> " + e.getValue().classType());
					}
				}

				spiClasses.put(type, Maps.newHashMap());

				LOG.info("Setting " + ClassUtils.toString(locator.getClass()) + " as the default SPI Locator for type: "
						+ type.name());

				defaultSpiLocator.put(type, locator);

			} else {

				LOG.info("Using existing SPI Locator, app = " + appId + " , type = " + type.toString());
			}

			addClasses(isNew, locator, appId);
		}
	}

	/**
	 * @param newLocator
	 * @param spi
	 * @param appId
	 * 
	 * @return {@code Boolean} Flag indicating whether a new locator was used
	 */
	private static boolean addClasses(boolean newLocator, BaseSPILocator spi, String appId) {

		LOG.info("Scanning SPI classes for type: " + spi.spiType());

		if (newLocator) {

			spiClasses.get(spi.spiType()).put(AppProvisioner.DEFAULT_APP_ID, scanClasses(spi, null));

			AppProvisioner.get().listApps().forEach(x -> {
				addClasses(spi, x);
			});

		} else {
			addClasses(spi, appId);
		}

		return newLocator;
	}

	private static void addClasses(BaseSPILocator spi, String appId) {

		registerLocatorSuffixes(appId, spi.spiType());

		List<Class<?>> classes = scanClasses(spi, AppProvisioner.get().getClassloader(appId));

		if (!classes.isEmpty()) {
			spiClasses.get(spi.spiType()).put(appId, classes);
		}
	}

	private static void registerLocatorSuffixes(String appId, SpiType type) {

		JsonObject config = AppProvisioner.get().getConfig(appId);

		JsonElement e = config.get("platform.spi." + type.toString().toLowerCase() + ".fileSuffixes");

		if (e == null || e.getAsString().trim().equals("")) {
			return;
		}

		String[] suffixes = e.getAsString().split(",[\\s]*");
		for (String suffix : suffixes) {
			BaseSPILocator.addTypeSuffix(type, suffix);
		}
	}

	private static List<Class<?>> scanClasses(BaseSPILocator spi, AppClassLoader cl) {

		List<Class<?>> classes = Lists.newArrayList();

		ClasspathScanner<?> cs = new ClasspathScanner<>(spi.classSuffix(), spi.classType(), spi.classIdentity())
				.setMaxCount(spi.spiType().getCount()).setClassLoader(cl).setShuffleStrategy(spi.shuffleStrategy());

		cs.scanClasses().forEach(c -> {
			classes.add(c);
		});

		return classes;
	}

	static Map<SpiType, BaseSPILocator> getDefaultLocators() {
		return defaultSpiLocator;
	}

	@Override
	public void exists(SpiType type, Class<?> clazz) {
		ObjectWrapper<Boolean> wrapper = new ObjectWrapper<Boolean>(false);

		spiClasses.get(type).forEach((appId, classes) -> {
			if (wrapper.get()) {
				return;
			}

			for (Class<?> c : classes) {
				if (c.equals(clazz)) {
					wrapper.set(true);
					return;
				}
			}
		});
	}

	static Map<SpiType, Map<String, List<Class<?>>>> getSpiClasses() {
		return spiClasses;
	}
}
