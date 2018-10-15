package com.re.paas.internal.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.app_provisioning.AppProvisioner;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiBase;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiDelegateHandler;
import com.re.paas.api.spi.SpiLocatorHandler;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.errors.ApplicationError;

public class SPILocatorHandlerImpl implements SpiLocatorHandler {

	private static final Logger LOG = Logger.get(BaseSPILocator.class);

	static Map<SpiTypes, BaseSPILocator> defaultSpiLocator = Collections
			.synchronizedMap(new HashMap<SpiTypes, BaseSPILocator>());

	static Map<SpiTypes, Map<String, List<Class<?>>>> spiClasses = Collections
			.synchronizedMap(new HashMap<SpiTypes, Map<String, List<Class<?>>>>());


	/**
	 * This is used to temporarily hold the result returned in last call to
	 * BaseSPILocator.start(..)
	 */
	static Map<SpiTypes, Boolean> locatorScanResult;

	
	/**
	 * After a call to SpiDelegate.start(..) has been made in SPIBase, we now know
	 * the dependency hierarchy of the delegates, so we therefore have to re-arrange
	 * the entries in the spiClasses map
	 */
	public void reshuffleClasses() {

		Map<SpiTypes, Map<String, List<Class<?>>>> spiClasses = Collections
				.synchronizedMap(new LinkedHashMap<SpiTypes, Map<String, List<Class<?>>>>());

		Map<SpiTypes, SpiDelegate<?>> delegatesMap = SpiDelegateHandler.get().getDelegates();
		
		SpiTypes[] types = new SpiTypes[delegatesMap.size()];
		Integer i = types.length - 1;

		for(SpiTypes type : delegatesMap.keySet()) {
			types[i] = type;
			i --;
		}
		
		assert i.equals(-1);

		for (SpiTypes type : types) {
			spiClasses.put(type, SPILocatorHandlerImpl.spiClasses.get(type));
		}

		synchronized (SPILocatorHandlerImpl.spiClasses) {
			SPILocatorHandlerImpl.spiClasses = spiClasses;
		}
	}

	public void start(String appId, SpiTypes[] types) {

		Map<SpiTypes, Boolean> result = new HashMap<>();

		for (SpiTypes type : types) {

			BaseSPILocator locator = defaultSpiLocator.get(type);

			KeyValuePair<String, ClassLoader> config = AppProvisioner.get().getConfiguration(appId,
					SpiBase.get().getLocatorConfigKey(type));

			if (config == null) {
				LOG.error("No key: " + SpiBase.get().getLocatorConfigKey(type) + " was found in configuration");
				continue;
			}

			boolean newLocator = locator == null || !config.getValue().equals(locator.getClass().getClassLoader());

			if (newLocator) {

				Class<? extends BaseSPILocator> locatorClass = null;
				try {
					locatorClass = ClassUtils.forName(config.getKey(), config.getValue());
				} catch (Exception e) {
					LOG.error("Could not find locator class: " + config.getKey());
					continue;
				}
				
				locator = ClassUtils.createInstance(locatorClass);

				LOG.info("Starting new SPI Locator, app = " + appId + " , type = " + type.toString());

				// Verify that declared spi type corresponds to type
				if (!(locator.spiType() == type)) {
					Exceptions.throwRuntime(PlatformException
							.get(ApplicationError.SERVICE_PROVIDER_CLASS_NOT_CONCRETE_IMPL, locatorClass.getName()));
				}

				// Verify that no other spi locator uses classType
				for (Entry<SpiTypes, BaseSPILocator> e : SPILocatorHandlerImpl.defaultSpiLocator.entrySet()) {
					if (locator.classType().isAssignableFrom(e.getValue().classType())) {
						Exceptions.throwRuntime(
								"Type: " + locator.classType().getName() + " is already defined for SpiType: "
										+ e.getKey() + " --> " + e.getValue().classType());
					}
				}
				
				if (appId.equals(AppProvisioner.get().defaultAppId())) {
					spiClasses.put(type, Maps.newHashMap());
				}

				LOG.info(
						"Setting " + locatorClass.getName() + " as the default SPI Locator for type: " + type.name());

				defaultSpiLocator.put(type, locator);

			} else {

				LOG.info("Using existing SPI Locator, app = " + appId + " , type = " + type.toString());
			}

			addClasses(newLocator, locator, appId);

			result.put(type, newLocator);
		}

		locatorScanResult = result;
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

			spiClasses.get(spi.spiType()).put(AppProvisioner.get().defaultAppId(), scanClasses(spi, null));

			AppProvisioner.get().listApps().forEach(x -> {

				List<Class<?>> classes = scanClasses(spi, AppProvisioner.get().getClassloader(x));

				if (!classes.isEmpty()) {
					spiClasses.get(spi.spiType()).put(x, classes);
				}

			});

		} else {

			// Note: It's okay for AppProvisioning.getClassloader(..) to return null
			List<Class<?>> classes = scanClasses(spi, AppProvisioner.get().getClassloader(appId));

			if (!classes.isEmpty()) {
				spiClasses.get(spi.spiType()).put(appId, classes);
			}

		}

		return newLocator;
	}

	private static List<Class<?>> scanClasses(BaseSPILocator spi, AppClassLoader cl) {

		List<Class<?>> classes = Lists.newArrayList();

		ClasspathScanner<?> cs = new ClasspathScanner<>(spi.classSuffix(), spi.classType(), spi.classIdentity())
				.setMaxCount(spi.spiType().getCount())
				.setClassLoader(cl)
				.setShuffleStrategy(spi.shuffleStrategy());

		cs.scanClasses().forEach(c -> {
			classes.add(c);
		});

		return classes;
	}

	@Override
	public Map<SpiTypes, BaseSPILocator> getDefaultLocators() {
		return defaultSpiLocator;
	}

	@Override
	public Map<SpiTypes, Map<String, List<Class<?>>>> getSpiClasses() {
		return spiClasses;
	}
	
	@Override
	public Map<SpiTypes, Boolean> getScanResult() {
		return locatorScanResult;
	}
}
