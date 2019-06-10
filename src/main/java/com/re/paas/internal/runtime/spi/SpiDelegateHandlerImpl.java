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
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.infra.cache.AbstractCacheAdapterDelegate;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.api.runtime.MethodMeta.Factor;
import com.re.paas.api.runtime.MethodMeta.IdentityStrategy;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.runtime.spi.TypeClassification;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.fusion.ui.impl.UIContext;
import com.re.paas.internal.infra.cache.CacheBackedMap;
import com.re.paas.internal.runtime.spi.SpiDelegateConfigHandler.Tier;

public class SpiDelegateHandlerImpl implements SpiDelegateHandler {

	private static final Logger LOG = Logger.get(SpiDelegateHandlerImpl.class);

	static Map<SpiType, SpiDelegate<?>> delegates = Collections
			.synchronizedMap(new LinkedHashMap<SpiType, SpiDelegate<?>>());

	private static Map<SpiType, Map<Object, Object>> resources = Collections
			.synchronizedMap(new HashMap<SpiType, Map<Object, Object>>());

	public SpiDelegateHandlerImpl() {
		SpiDelegateConfigHandler.init();
	}

	private static Map<SpiType, Class<? extends SpiDelegate<?>>> scanAvailableDelegates(ClassLoader cl) {

		LOG.debug("Scanning for available delegates.. ");

		Map<SpiType, Class<? extends SpiDelegate<?>>> result = new HashMap<>(SpiType.values().length);

		for (SpiType type : SpiType.values()) {

			BaseSPILocator locator = SPILocatorHandlerImpl.getDefaultLocators().get(type);

			@SuppressWarnings("unchecked")
			Class<SpiDelegate<?>> delegateType = (Class<SpiDelegate<?>>) locator.delegateType();

			List<Class<? extends SpiDelegate<?>>> delegateClasses = new ClasspathScanner<>(delegateType,
					ClassIdentityType.ASSIGNABLE_FROM).setMaxCount(1).setClassLoader(cl).scanClasses();

			if (delegateClasses.isEmpty()) {
				result.put(type, null);
			} else {
				result.put(type, delegateClasses.get(0));
			}
		}

		return result;
	}

	private static Boolean shouldSetAsDefault(String appId, SpiType type) {

		String appName = AppProvisioner.get().getAppName(appId);

		String confirmMessage = ClientRBRef.get("app.confirm.set.default.delegate").add(type + ".action").toString();

		boolean useAsDefault = UIContext.confirm(appName, confirmMessage);

		if (useAsDefault && type.classification() == TypeClassification.ACTIVE) {

			String confirmTrust = ClientRBRef.get("app.confirm.trust.set.default.delegate").toString();
			useAsDefault = UIContext.confirm(appName, confirmTrust);

			if (useAsDefault) {
				SpiBaseImpl.registerTrustedApp(appId);
			}
		}

		return useAsDefault;
	}

	/**
	 * This scans for available delegates and registers them on the platform
	 * 
	 * @param cl
	 */
	static void install(ClassLoader cl) {

		String appId = ClassLoaders.getId(cl);

		// Scan available delegates
		Map<SpiType, Class<? extends SpiDelegate<?>>> available = scanAvailableDelegates(cl);

		// Add delegate in available and/or default tier(s), based on user preference
		for (Entry<SpiType, Class<? extends SpiDelegate<?>>> e : available.entrySet()) {
			Tier[] tiers = shouldSetAsDefault(appId, e.getKey()) ? Tier.onlyDefaultAndAvailable()
					: Tier.onlyAvailable();

			LOG.debug("Registering delegate " + e.getValue() + ", type=" + e.getValue() + ", tiers="
					+ Arrays.toString(tiers));
			SpiDelegateConfigHandler.put(e.getKey(), e.getValue(), tiers);
		}
	}

	static void uninstall(ClassLoader cl) {

		// Scan available delegates
		Map<SpiType, Class<? extends SpiDelegate<?>>> available = scanAvailableDelegates(cl);

		// Remove delegate from available and default
		for (Entry<SpiType, Class<? extends SpiDelegate<?>>> e : available.entrySet()) {

			Tier[] tiers = SpiDelegateConfigHandler.get(e.getKey(), Tier.onlyDefault()) != null
					? Tier.onlyDefaultAndAvailable()
					: Tier.onlyAvailable();

			LOG.debug("Unregistering delegate " + e.getValue() + ", type=" + e.getValue() + ", tiers="
					+ Arrays.toString(tiers));
			SpiDelegateConfigHandler.remove(e.getKey(), e.getValue(), tiers);
		}
	}

	@BlockerTodo("use DelegateInitResult as returned by the delegate's init function")
	static void start(ClassLoader cl, SpiType[] types, String dependants) {

		for (SpiType type : types) {

			SpiDelegate<?> delegate = delegates.get(type);

			Boolean isDelegateLoaded = delegate != null;

			if (!isDelegateLoaded) {

				delegate = ClassUtils.createInstance(SpiDelegateConfigHandler.get(type, Tier.all()));

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

							start(cl, new SpiType[] { dependency }, dependants + " -> " + type.name());

						} else {
							start(cl, new SpiType[] { dependency }, type.name());
						}

					}
				}

				if (!delegate.applies()) {
					continue;
				}

				LOG.info("Starting new SPI Delegate, type = " + type.toString());

				@SuppressWarnings("unchecked")
				Class<SpiDelegate<?>> delegateType = (Class<SpiDelegate<?>>) locator.delegateType();

				// Register delegate instance as singleton
				Singleton.register(delegateType, delegate);

				if (delegates.containsKey(type)) {
					delegates.remove(type).shutdown();
				}

				delegates.put(type, delegate);

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

				// Initialize delegate
				DelegateInitResult r = startDelegate(delegate);

				// Todo: Process result

			} else {

				// Transfer only application-specific classes to the existing delegate, since
				// other classes must have been registered when the delegate was instantiated

				String appId = ClassLoaders.getId(cl);

				List<Class<?>> classes = SPILocatorHandlerImpl.getSpiClasses().get(type).get(appId);

				delegate.add0(classes);
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

	private static DelegateInitResult startDelegate(SpiDelegate<?> delegate) {

		boolean isTrusted = isTrusted(delegate);

		if (!isTrusted && delegate.getType().getKey().classification() == TypeClassification.ACTIVE) {
			throw new SecurityException("Only trusted Spi delegates can be used for an active Spi Type");
		}

		return delegate.init();
	}

	private static boolean isTrusted(SpiDelegate<?> delegate) {

		@SuppressWarnings("unchecked")
		Class<? extends SpiDelegate<?>> delegateClass = (Class<? extends SpiDelegate<?>>) delegate.getClass();

		return ClassUtils.isTrusted(delegateClass) || SpiBaseImpl.isAppTrusted(ClassUtils.getAppId(delegateClass));
	}

	@Override
	public Map<SpiType, SpiDelegate<?>> getDelegates() {
		return delegates;
	}

	public Map<Object, Object> getResources(SpiType type) {
		return resources.get(type);
	}

	@BlockerBlockerTodo("Ensure that the SPIs are tranversed in the correct order")
	@MethodMeta(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = { SpiDelegate.class })
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
			LOG.warn("No resource class(es) were registered for the SpiType: " + type.name());
		}
	}
}
