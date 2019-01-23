package com.re.paas.internal.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.DelegateSpec;
import com.re.paas.api.spi.SpiBase;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiDelegateHandler;
import com.re.paas.api.spi.SpiLocatorHandler;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.spi.TypeClassification;
import com.re.paas.api.threadsecurity.ThreadSecurity;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.app_provisioning.AppProvisioner;
import com.re.paas.internal.infra.cache.CacheBackedMap;

public class SpiDelegateHandlerImpl implements SpiDelegateHandler {

	private static final Logger LOG = Logger.get(SpiDelegateHandlerImpl.class);

	private static Map<SpiTypes, SpiDelegate<?>> delegates = Collections
			.synchronizedMap(new LinkedHashMap<SpiTypes, SpiDelegate<?>>());

	private static Map<SpiTypes, Map<Object, Object>> resources = Collections
			.synchronizedMap(new HashMap<SpiTypes, Map<Object, Object>>());

	@BlockerTodo("Add support for making external delegates to be trusted")
	public final void start(String dependants, String appId, SpiTypes[] types) {

		for (SpiTypes type : types) {

			SpiDelegate<?> delegate = delegates.get(type);

			KeyValuePair<String, ClassLoader> config = AppProvisioner.get().getConfiguration(appId,
					SpiBase.get().getDelegateConfigKey(type));

			if (config == null) {
				LOG.info("No key: " + SpiBase.get().getDelegateConfigKey(type) + " was found in configuration");
				continue;
			}

			boolean newDelegate = delegate == null || !config.getValue().equals(delegate.getClass().getClassLoader());

			if (newDelegate) {

				Class<? extends SpiDelegate<?>> delegateClass = null;
				try {
					delegateClass = ClassUtils.forName(config.getKey(), config.getValue());
				} catch (Exception e) {
					LOG.error("Could not find delegate class: " + config.getKey());
					continue;
				}

				delegate = ClassUtils.createInstance(delegateClass);

				/**
				 * This reads the generic types declared for this class, and registers the
				 * corresponding SpiType
				 */

				Class<?> delegateLocatorClassType = delegate.getLocatorClassType();

				// Get the spiType that delegateLocatorClassType represents

				for (Entry<SpiTypes, BaseSPILocator> e : SpiLocatorHandler.get().getDefaultLocators().entrySet()) {
					if (delegateLocatorClassType.isAssignableFrom(e.getValue().classType())) {
						delegate.setType(new KeyValuePair<SpiTypes, Class<?>>(e.getKey(), delegateLocatorClassType));
						break;
					}
				}

				if (delegate.getType() == null) {
					Exceptions.throwRuntime("The specified generic type do not conform to any SpiType");
				}

				if (!delegate.getSpiType().equals(type)) {
					Exceptions.throwRuntime(
							"Class: " + delegateClass.getName() + " is not a delegate for SpiType: " + type.name());
				}

				BaseSPILocator locator = SpiLocatorHandler.get().getDefaultLocators().get(type);

				if (!locator.delegateType().isAssignableFrom(delegateClass)) {
					Exceptions.throwRuntime("Class: " + delegateClass.getName() + " is not a concrete subclass of "
							+ locator.delegateType().getName() + " as defined by " + locator.getClass().getName());
				}

				SpiTypes[] deps = getDependencies(delegateClass);

				if (deps.length > 0) {

					for (SpiTypes dependency : deps) {

						if (delegates.containsKey(dependency)) {
							continue;
						}

						if (dependants != null) {
							if (dependants.contains(dependency.name())) {
								// Circular reference was detected
								Exceptions.throwRuntime(new RuntimeException("Circular reference was detected: "
										+ (dependants + " -> " + type.name() + " -> " + dependency.name())
												.replaceAll(dependency.name(), "(" + dependency.name() + ")")));
							}

							start(dependants + " -> " + type.name(), appId, new SpiTypes[] { dependency });

						} else {
							start(type.name(), appId, new SpiTypes[] { dependency });
						}

					}
				}

				Logger.get().info("Starting new SPI Delegate, app = " + appId + " , type = " + type.toString());

				@SuppressWarnings("unchecked")
				Class<SpiDelegate<?>> delegateType = (Class<SpiDelegate<?>>) locator.delegateType();

				// Register delegate instance as singleton
				Singleton.register(delegateType, delegate);

				if (delegates.containsKey(type)) {
					delegates.remove(type).destroy();
				}

				delegates.put(type, delegate);

				// Clear resource from previous delegate
				if (resources.containsKey(type)) {
					resources.get(type).clear();
				}

				// Create resource map

				// TODO Here, Check if delegate is respecting the setting in Cloud Environment

				if (!delegate.inMemory() && !Arrays.asList(deps).contains(SpiTypes.CACHE_ADAPTER)) {
					Exceptions.throwRuntime(ClassUtils.toString(delegateClass) + " must have a dependency on "
							+ SpiTypes.CACHE_ADAPTER);
				}

				Map<Object, Object> rMap = null;

				// Create resource map
				if (delegate.inMemory()) {
					rMap = new HashMap<>();
				} else {
					CacheFactory<String, Object> factory = ((AbstractCacheAdapterDelegate) delegates
							.get(SpiTypes.CACHE_ADAPTER)).getCacheFactory();
					rMap = new CacheBackedMap<>(factory);
				}

				resources.put(type, rMap);

				// Here, we have a new delegate, so we are adding all discovered classes
				// across all applications

				List<Class<?>> classes = SpiLocatorHandler.get().getSpiClasses().get(type).get(appId);

				// Determine whether this delegate is trusted
				// TODO Add support for making external delegates to be trusted
				boolean isTrusted = ClassUtils.isTrusted(delegateClass);

				// Initialize delegate
				initDelegate(isTrusted, type, delegate, classes);

			} else {

				// In this scenario, we are adding the discovered classes specifically
				// owned by appId, to the current delegate, just so that it's aware

				// See SpiBaseImpl#stop0(..);

				Logger.get().info("Using existing SPI Delegate, app = " + appId + " , type = " + type.toString());

				if (SpiLocatorHandler.get().getScanResult().get(type)) {
					// This implies that a new locator was installed for type
					// Unload classes discovered by previous locator
					delegate.unload();
				}

				List<Class<?>> classes = SpiLocatorHandler.get().getSpiClasses().get(type).get(appId);

				if (classes != null && !classes.isEmpty()) {
					delegate.add0(classes);
				}
			}

			// #foreach

		}
	}

	private static SpiTypes[] getDependencies(Class<? extends SpiDelegate<?>> delegateClass) {

		List<SpiTypes> deps = new ArrayList<>();

		// Add dependencies from subclasses
		ClassUtils.forEachInTree(delegateClass, SpiDelegate.class, (c) -> {

			// Add declared dependencies
			DelegateSpec spec = c.getAnnotation(DelegateSpec.class);

			if (spec != null) {
				for (SpiTypes t : spec.dependencies()) {
					deps.add(t);
				}
			}
		});

		return Utils.toArray(deps);
	}

	private static void initDelegate(boolean isTrusted, SpiTypes type, SpiDelegate<?> delegate,
			List<Class<?>> classes) {

		Class<?> delegateClass = delegate.getClass();

		if (!isTrusted && type.classification() == TypeClassification.ACTIVE) {
			throw new SecurityException("Only trusted Spi delegates can be used for an active Spi Type");
		}

		// Start delegate

		if (!isTrusted) {

			ThreadSecurity.get().runCommon(() -> {

				delegate.init();
				
			}, (AppClassLoader) delegateClass.getClassLoader());

		} else {

			ThreadSecurity.get().runTrusted(() -> {

				delegate.init();
			});
		}

	}

	@BlockerBlockerTodo("Ensure that the SPIs are tranversed in the correct order")
	public final void forEach(SpiTypes type, Consumer<Class<?>> consumer) {

		loop: for (List<Class<?>> l : SpiLocatorHandler.get().getSpiClasses().get(type).values()) {
			for (Class<?> c : l) {
				consumer.accept(c);
				if (type.getCount() != -1 && resources.get(type).size() == type.getCount()) {
					Logger.get().info("All resources has been added successfully for SpiType: " + type.name() + " ..");
					break loop;
				}
			}
		}

		// Validate resource count
		Map<Object, Object> o = resources.get(type);

		if (type.getCount() != -1 && o.size() > type.getCount()) {
			Exceptions.throwRuntime("SpiType: " + type.name() + " should have " + type.getCount() + " resource(s)");
		}

		if (o.isEmpty()) {
			Logger.get().warn("No resources were registered for the SpiType: " + type.name());
		}

	}

	@Override
	public Map<SpiTypes, SpiDelegate<?>> getDelegates() {
		return delegates;
	}

	@Override
	public Map<SpiTypes, Map<Object, Object>> getResources() {
		return resources;
	}

}
