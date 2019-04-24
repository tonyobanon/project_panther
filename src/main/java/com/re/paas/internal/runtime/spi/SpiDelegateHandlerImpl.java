package com.re.paas.internal.runtime.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.runtime.spi.TypeClassification;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.AppDirectory;
import com.re.paas.internal.infra.cache.CacheBackedMap;

public class SpiDelegateHandlerImpl implements SpiDelegateHandler {

	private static final Logger LOG = Logger.get(SpiDelegateHandlerImpl.class);

	private static Map<SpiType, SpiDelegate<?>> delegates = Collections
			.synchronizedMap(new LinkedHashMap<SpiType, SpiDelegate<?>>());

	private static Map<SpiType, Map<Object, Object>> resources = Collections
			.synchronizedMap(new HashMap<SpiType, Map<Object, Object>>());

	private static SpiDelegate<?> findDelegate(SpiType type) {

		Class<? extends SpiDelegate<?>> delegate = SpiBaseImpl.getDefaultDelegate(type);

		if (delegate == null) {

			String key = SpiBaseImpl.getDelegateConfigKey(type);
			delegate = ClassUtils.forName(AppDirectory.getConfig().get(key).getAsString());
		}

		return ClassUtils.createInstance(delegate);
	}

	private static boolean isDelegateTrusted(Class<? extends SpiDelegate<?>> delegateClass) {
		return ClassUtils.isTrusted(delegateClass) || SpiBaseImpl.isTrusted(ClassUtils.getAppId(delegateClass));
	}

	@BlockerTodo("use DelegateInitResult as returned by the delegate's init function")
	static void start(String dependants, String appId, SpiType[] types) {

		for (SpiType type : types) {

			SpiDelegate<?> delegate = delegates.get(type);

			SpiDelegate<?> newDelegate = findDelegate(type);

			boolean isNewDelegate = delegate == null || !ClassUtils.equals(delegate.getClass(), newDelegate.getClass());

			if (isNewDelegate) {

				delegate = newDelegate;

				@SuppressWarnings("unchecked")
				Class<? extends SpiDelegate<?>> delegateClass = (Class<? extends SpiDelegate<?>>) delegate.getClass();

				/**
				 * This reads the generic types declared for this class, and registers the
				 * corresponding SpiType
				 */

				Class<?> delegateLocatorClassType = delegate.getLocatorClassType();

				// Get the spiType that delegateLocatorClassType represents

				for (Entry<SpiType, BaseSPILocator> e : SPILocatorHandlerImpl.getDefaultLocators().entrySet()) {
					if (delegateLocatorClassType.isAssignableFrom(e.getValue().classType())) {
						delegate.setType(new KeyValuePair<SpiType, Class<?>>(e.getKey(), delegateLocatorClassType));
						break;
					}
				}

				if (delegate.getType() == null) {
					Exceptions.throwRuntime("The specified generic type do not conform to any SpiType");
				}

				if (!delegate.getSpiType().equals(type)) {
					Exceptions.throwRuntime("Class: " + ClassUtils.toString(delegateClass)
							+ " is not a delegate for SpiType: " + type.name());
				}

				BaseSPILocator locator = SPILocatorHandlerImpl.getDefaultLocators().get(type);

				if (!locator.delegateType().isAssignableFrom(delegateClass)) {
					Exceptions.throwRuntime("Class: " + ClassUtils.toString(delegateClass)
							+ " is not a concrete subclass of " + locator.delegateType().getName() + " as defined by "
							+ locator.getClass().getName());
				}

				SpiType[] deps = getDependencies(delegateClass);

				if (deps.length > 0) {

					for (SpiType dependency : deps) {

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

							start(dependants + " -> " + type.name(), appId, new SpiType[] { dependency });

						} else {
							start(type.name(), appId, new SpiType[] { dependency });
						}

					}
				}

				LOG.info("Starting new SPI Delegate, app = " + appId + " , type = " + type.toString());

				@SuppressWarnings("unchecked")
				Class<SpiDelegate<?>> delegateType = (Class<SpiDelegate<?>>) locator.delegateType();

				// Register delegate instance as singleton
				Singleton.register(delegateType, delegate);

				if (delegates.containsKey(type)) {
					delegates.remove(type).shutdown();
				}

				delegates.put(type, delegate);

				// Clear resource from previous delegate
				if (resources.containsKey(type)) {
					resources.get(type).clear();
				}

				// TODO Here, Check if delegate is respecting the setting in Cloud Environment

				if (!delegate.inMemory() && !Arrays.asList(deps).contains(SpiType.CACHE_ADAPTER)) {
					Exceptions.throwRuntime(
							ClassUtils.toString(delegateClass) + " must have a dependency on " + SpiType.CACHE_ADAPTER);
				}

				// Create resource map

				Map<Object, Object> rMap = null;

				// Create resource map
				if (delegate.inMemory()) {
					rMap = new HashMap<>();
				} else {
					CacheFactory<String, Object> factory = ((AbstractCacheAdapterDelegate) delegates
							.get(SpiType.CACHE_ADAPTER)).getCacheFactory();
					rMap = new CacheBackedMap<>(factory);
				}

				resources.put(type, rMap);

				// Here, we have a new delegate, so we are adding all discovered classes
				// across all applications

				List<Class<?>> classes = SPILocatorHandlerImpl.getSpiClasses().get(type).get(appId);

				// Initialize delegate
				DelegateInitResult r = initDelegate(type, delegate, classes);
				
				
				

			} else {

				// In this scenario, we are adding the discovered classes specifically
				// owned by appId, to the current delegate, just so that it's aware

				// See SpiBaseImpl#stop0(..);

				LOG.info("Using existing SPI Delegate, app = " + appId + " , type = " + type.toString());

				List<Class<?>> classes = SPILocatorHandlerImpl.getSpiClasses().get(type).get(appId);

				if (classes != null && !classes.isEmpty()) {
					delegate.add0(classes);
				}
			}
		}
	}

	private static SpiType[] getDependencies(Class<? extends SpiDelegate<?>> delegateClass) {

		List<SpiType> deps = new ArrayList<>();

		// Add dependencies from subclasses
		ClassUtils.forEachInTree(delegateClass, SpiDelegate.class, (c) -> {

			// Add declared dependencies
			DelegateSpec spec = c.getAnnotation(DelegateSpec.class);

			if (spec != null) {
				for (SpiType t : spec.dependencies()) {
					deps.add(t);
				}
			}
		});

		return deps.toArray(new SpiType[deps.size()]);
	}

	private static DelegateInitResult initDelegate(SpiType type, SpiDelegate<?> delegate, List<Class<?>> classes) {

		@SuppressWarnings("unchecked")
		Class<? extends SpiDelegate<?>> delegateClass = (Class<? extends SpiDelegate<?>>) delegate.getClass();

		boolean isTrusted = isDelegateTrusted(delegateClass);

		if (!isTrusted && type.classification() == TypeClassification.ACTIVE) {
			throw new SecurityException("Only trusted Spi delegates can be used for an active Spi Type");
		}

		// Start delegate
		DelegateInitResult r = (DelegateInitResult) ExecutorFactory.get().execute(delegate::init, isTrusted,
				isTrusted ? null : (AppClassLoader) delegateClass.getClassLoader()).join();
		
		return r;
	}

	@Override
	public Map<SpiType, SpiDelegate<?>> getDelegates() {
		return delegates;
	}

	@Override
	public Map<Object, Object> getResources(SpiType type) {
		return resources.get(type);
	}

	@BlockerBlockerTodo("Ensure that the SPIs are tranversed in the correct order")
	public final void forEach(SpiType type, Consumer<Class<?>> consumer) {
		
		loop: for (List<Class<?>> l : SPILocatorHandlerImpl.getSpiClasses().get(type).values()) {
			for (Class<?> c : l) {
				consumer.accept(c);
				if (type.getCount() != -1 && resources.get(type).size() == type.getCount()) {
					LOG.info("All resources has been added successfully for SpiType: " + type.name() + " ..");
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
			LOG.warn("No resources were registered for the SpiType: " + type.name());
		}
	}

}
