package com.re.paas.internal.spi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.app_provisioning.AppProvisioner;
import com.re.paas.api.cache.CacheBackedMap;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.DelegateSpec;
import com.re.paas.api.spi.SpiBase;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiDelegateHandler;
import com.re.paas.api.spi.SpiLocatorHandler;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.utils.ClassUtils;

public class SpiDelegateHandlerImpl implements SpiDelegateHandler {
	
	private static final Logger LOG = Logger.get(SpiDelegateHandlerImpl.class);

	private static Map<SpiTypes, SpiDelegate<?>> delegates = Collections
			.synchronizedMap(new LinkedHashMap<SpiTypes, SpiDelegate<?>>());

	private static Map<SpiTypes, Map<Object, Object>> resources = Collections
			.synchronizedMap(new HashMap<SpiTypes, Map<Object, Object>>());

	public final void start(String dependants, String appId, SpiTypes[] types) {

		Map<SpiTypes, SpiDelegate<?>> newDelegates = new HashMap<>();
		
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

				DelegateSpec spec = delegateClass.getAnnotation(DelegateSpec.class);

				if (spec != null && spec.dependencies().length > 0) {

					for (SpiTypes dependency : spec.dependencies()) {

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
				
				
				
				// #Error here: Singleton.register is potentially called multiple times
				Singleton.register(delegateType, delegate);
				
				if (delegates.containsKey(type)) {
					delegates.remove(type).destroy();
				}

				delegates.put(type, delegate);

				if (resources.containsKey(type)) {
					resources.get(type).clear();
				}
				
				// Create resource map
				
				// Here, Check if delegate is respecting the setting in Cloud Environment
				
				Map<Object, Object> rMap = delegate.inMemory() ? new HashMap<>() : new CacheBackedMap<>();				
				resources.put(type, rMap);
				
								
				boolean isInternalDelegate = delegateClass.getClassLoader() instanceof AppClassLoader;
				
				
				// Start delegate
				delegate.init();
				
				// Here, we have a new delegate, so we are adding all discovered classes
				// across all applications
				
				List<Class<?>> classes = SpiLocatorHandler.get().getSpiClasses().get(type).get(appId);

				if (classes != null && !classes.isEmpty()) {
					delegate.add0(classes);
				}
				
				System.clearProperty("java.nio.file.spi.DefaultFileSystemProvider");


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
			
			//#foreach
			
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
