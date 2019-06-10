package com.re.paas.internal.runtime.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiLocatorHandler;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.errors.ApplicationError;

public class SPILocatorHandlerImpl implements SpiLocatorHandler {

	private static final Logger LOG = Logger.get(SPILocatorHandlerImpl.class);

	static Map<SpiType, BaseSPILocator> defaultSpiLocator = Collections
			.synchronizedMap(new HashMap<SpiType, BaseSPILocator>());

	static Map<SpiType, Map<String, List<Class<?>>>> spiClasses = Collections
			.synchronizedMap(new HashMap<SpiType, Map<String, List<Class<?>>>>());

	private static BaseSPILocator findLocator(SpiType type) {
		String key = SPILocatorHandlerImpl.getLocatorConfigKey(type);
		return ClassUtils.createInstance(ClassLoaders.getConfiguration().getString(key));
	}

	static void start(ClassLoader cl, SpiType[] types) {

		addLocatorSuffixes(cl);

		for (SpiType type : types) {

			BaseSPILocator locator = defaultSpiLocator.get(type);

			boolean isLocatorLoaded = locator != null;
			
			if (!isLocatorLoaded) {

				locator = findLocator(type);

				LOG.debug("Adding SPI Locator" + locator + " for type: " + type.toString());

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
				defaultSpiLocator.put(type, locator);
			}

			addClasses(locator, cl);
		}
	}

	/**
	 * @param newLocator
	 * @param spi
	 * @param appId
	 * 
	 * @return {@code Boolean} Flag indicating whether a new locator was used
	 */
	private static void addClasses(BaseSPILocator spi, ClassLoader cl) {

		ClasspathScanner<?> cs = new ClasspathScanner<>(spi.classSuffix(), spi.classType(), spi.classIdentity())
				.setMaxCount(spi.spiType().getCount()).setClassLoader(cl).setLoadAbstractClasses(true)
				.setShuffleStrategy(spi.shuffleStrategy());

		List<?> classes = cs.scanClasses();

		if (!classes.isEmpty()) {
			@SuppressWarnings("unchecked")
			List<Class<?>> o = (List<Class<?>>) classes;
			spiClasses.get(spi.spiType()).put(ClassLoaders.getId(cl), o);
		}
	}

	static Map<SpiType, BaseSPILocator> getDefaultLocators() {
		return defaultSpiLocator;
	}

	@Override
	public Boolean exists(SpiType type, Class<?> clazz) {
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
		return wrapper.get();
	}

	static String getLocatorConfigKey(SpiType type) {
		return "platform.spi." + type.toString().toLowerCase() + ".locator";
	}

	static Map<SpiType, Map<String, List<Class<?>>>> getSpiClasses() {
		return spiClasses;
	}

	private static Map<SpiType, String[]> getLocatorSuffixes(JsonObject config) {

		Map<SpiType, String[]> result = new HashMap<>(SpiType.values().length);

		for (SpiType type : SpiType.values()) {

			String e = config.getString("platform.spi." + type.toString().toLowerCase() + ".fileSuffixes");

			if (e == null || e.trim().equals("")) {
				result.put(type, null);
			} else {
				String[] suffixes = e.split("[\\s]*,[\\s]*");
				result.put(type, suffixes);
			}
		}

		return result;
	}

	private static void addLocatorSuffixes(ClassLoader cl) {

		String appId = ClassLoaders.getId(cl);
		JsonObject config = ClassLoaders.getConfiguration(cl);

		for(Entry<SpiType, String[]> e : getLocatorSuffixes(config).entrySet()) {
			if (e.getValue() != null) {
				BaseSPILocator.addTypeSuffix(e.getKey(), appId, e.getValue());
			}
		}
	}
}
