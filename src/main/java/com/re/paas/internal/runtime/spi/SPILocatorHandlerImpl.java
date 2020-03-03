package com.re.paas.internal.runtime.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.re.paas.api.apps.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiLocatorHandler;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.ClassUtil;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.errors.ApplicationError;

public class SPILocatorHandlerImpl implements SpiLocatorHandler {

	private static final Logger LOG = Logger.get(SPILocatorHandlerImpl.class);

	private static Map<SpiType, BaseSPILocator> defaultSpiLocator = Collections
			.synchronizedMap(new HashMap<SpiType, BaseSPILocator>());

	static Map<SpiType, Map<String, List<Class<?>>>> spiClasses = Collections
			.synchronizedMap(new HashMap<SpiType, Map<String, List<Class<?>>>>());

	static Map<String, List<String>> appDependencies = new HashMap<>();

	private static BaseSPILocator findLocator(SpiType type) {
		String key = SPILocatorHandlerImpl.getLocatorConfigKey(type);
		return ClassUtil.createInstance(ClassLoaders.getConfiguration().getString(key));
	}

	static void start(ClassLoader cl, SpiType[] types) {

		addLocatorSuffixes(cl);

		for (SpiType type : types) {

			if (cl instanceof AppClassLoader && type.classification().requiresTrustedResource()) {
				continue;
			}

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

	private static void addClasses(BaseSPILocator spi, ClassLoader cl) {

		if (!spi.scanResourceClasses()) {

			LOG.debug("Skipping resource classes scanning for type: " + spi.spiType());
			return;
		}

		ClasspathScanner<?> cs = new ClasspathScanner<>(spi.classSuffix(), spi.classType(), spi.classIdentity())
				.setMaxCount(spi.spiType().getCount()).setClassLoader(cl).setLoadAbstractClasses(true)
				.setShuffleStrategy(spi.shuffleStrategy());

		List<?> classes = cs.scanClasses();

		@SuppressWarnings("unchecked")
		List<Class<?>> o = (List<Class<?>>) classes;
		String appId = ClassLoaders.getId(cl);

		spiClasses.get(spi.spiType()).put(appId, o);
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

			Object v = config.getValue("platform.spi." + type.toString().toLowerCase() + ".fileSuffixes");

			if (v == null) {
				result.put(type, null);
			} else {

				List<String> values = null;

				if (v instanceof JsonArray) {

					JsonArray arrayValue = (JsonArray) v;
					
					values = Lists.newArrayList(arrayValue.iterator()).stream().map(a -> (String) a)
							.collect(Collectors.toUnmodifiableList());

				} else if (v instanceof String) {
					values = Lists.newArrayList((String) v);
				}

				result.put(type, values.toArray(new String[values.size()]));

			}
		}

		return result;
	}

	private static void addLocatorSuffixes(ClassLoader cl) {

		String appId = ClassLoaders.getId(cl);
		JsonObject config = ClassLoaders.getConfiguration(cl);

		for (Entry<SpiType, String[]> e : getLocatorSuffixes(config).entrySet()) {
			if (e.getValue() != null) {
				BaseSPILocator.addTypeSuffix(e.getKey(), appId, e.getValue());
			}
		}
	}

	@Override
	public void addDependencyPath(String source, List<String> targets) {
		addDependencyPath0(source, targets);
	}

	static void addDependencyPath0(String child, List<String> parents) {

		List<String> deps = appDependencies.get(child);

		// Check for circular dependency

		parents.forEach(p -> {
			if (hasDependency(p, child)) {
				Exceptions.throwRuntime("Detected circular dependency between apps: " + p + " and " + child);
			}
		});

		if (deps == null) {
			deps = new ArrayList<>();
			appDependencies.put(child, deps);
		}

		deps.addAll(parents);
	}

	private static boolean hasDependency(String child, String parent) {
		List<String> deps = appDependencies.get(child);
		return deps != null && deps.contains(parent);
	}

	static List<String> getDependants(String appId) {

		List<String> r = new ArrayList<>();

		AppProvisionerImpl.listApps0().forEach(a -> {
			if (hasDependency(a, appId)) {
				r.add(a);
			}
		});

		return r;
	}
}
