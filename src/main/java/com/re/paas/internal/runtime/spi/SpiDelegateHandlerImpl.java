package com.re.paas.internal.runtime.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.infinispan.configuration.cache.ConfigurationChildBuilder;
import org.infinispan.manager.DefaultCacheManager;

import com.re.paas.api.Platform;
import com.re.paas.api.Platform.State;
import com.re.paas.api.Singleton;
import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.AsyncDistributedMap;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.runtime.SecureMethod.Factor;
import com.re.paas.api.runtime.SecureMethod.IdentityStrategy;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.AppProvisioner;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourcesInitResult;
import com.re.paas.api.runtime.spi.DelegateResorceSet;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.DistributedStoreConfig;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiDelegateHandler;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.CacheBackedMap;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.clustering.ClusteringServicesImpl;
import com.re.paas.internal.fusion.UIContext;
import com.re.paas.internal.runtime.spi.SpiDelegateConfigHandler.Tier;

public class SpiDelegateHandlerImpl implements SpiDelegateHandler {

	private static final Logger LOG = Logger.get(SpiDelegateHandlerImpl.class);

	static Map<SpiType, SpiDelegate<?>> delegates = Collections
			.synchronizedMap(new LinkedHashMap<SpiType, SpiDelegate<?>>());

	private static Map<SpiType, DelegateResorceSet> resources = Collections
			.synchronizedMap(new HashMap<SpiType, DelegateResorceSet>());

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
					ClassIdentityType.ASSIGNABLE_FROM, cl).setMaxCount(1).scanClasses();

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

		boolean useAsDefault = UIContext.confirm(appName, "{app.confirm.set.default.delegate}", type.toString());

		if (useAsDefault && type.classification().requiresTrustedDelegate() && !SpiBaseImpl.isAppTrusted(appId)) {

			useAsDefault = UIContext.confirm(appName, "{app.confirm.trust}");

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

	@SuppressWarnings("unchecked")
	@BlockerTodo("use DelegateInitResult as returned by the delegate's init function")
	@BlockerTodo("Implement persistence as defined in DistributedStoreConfig")

	static Boolean start(ClassLoader cl, SpiType[] types, String dependants) {
		
		for (SpiType type : types) {

			SpiDelegate<?> delegate = delegates.get(type);

			Boolean isDelegateLoaded = delegate != null;

			if (!isDelegateLoaded) {
				

				delegate = ClassUtils.createInstance(SpiDelegateConfigHandler.get(type, Tier.all()));
				
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
					Exceptions.throwRuntime("The specified generic type do not conform to any SpiType: " + delegateLocatorClassType.getName());
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

				Class<SpiDelegate<?>> delegateType = (Class<SpiDelegate<?>>) locator.delegateType();

				// Register delegate instance as singleton
				Singleton.register(delegateType, delegate);

				if (delegates.containsKey(type)) {
					delegates.remove(type).shutdown();
				}

				delegates.put(type, delegate);

				// TODO Here, Check if delegate is respecting the setting in Cloud Environment

				if (delegate.requiresDistributedStore() && !Arrays.asList(deps).contains(SpiType.NODE_ROLE)) {
					Exceptions.throwRuntime(
							ClassUtils.toString(delegateClass) + " must have a dependency on " + SpiType.NODE_ROLE);
				}

				Map<Object, Object> inMemoryStore = new HashMap<>();
				Map<String, AsyncDistributedMap<String, Object>> distributedStores = null;

				if (delegate.requiresDistributedStore() && ClusteringServices.get().isStarted()) {

					List<Object> stores = delegate.distributedStoreNames();

					if (stores.isEmpty()) {
						Exceptions.throwRuntime(
								"No distributed store names specified in: " + ClassUtils.toString(delegateClass));
					}

					distributedStores = new HashMap<>(stores.size());

					for (Object store : stores) {

						DistributedStoreConfig config = store instanceof String
								? new DistributedStoreConfig((String) store)
								: (DistributedStoreConfig) store;

						String cacheName = config.getName();

						DefaultCacheManager cm = (DefaultCacheManager) ClusteringServices.get().getCacheManager();

						ConfigurationChildBuilder cacheConfig = ClusteringServicesImpl.getDefaultCacheConfiguration();

						cm.defineConfiguration(cacheName,
								config.getAlwaysReturnValue()
										? cacheConfig.unsafe().unreliableReturnValues(true).build()
										: cacheConfig.build());

						// Create or get multimap cache

						AsyncDistributedMap<String, Object> cache = new CacheBackedMap(cm.getCache(cacheName, true));

						// Note: No need to cm.defineConfiguration(...), since the default
						// configuration configures cache mode to DIST_SYNC

						distributedStores.put(cacheName, cache);
					}
				}

				resources.put(type, new DelegateResourceSetImpl(inMemoryStore, distributedStores));

				// Initialize delegate
				DelegateInitResult r = startDelegate(delegate);

				if (r == DelegateInitResult.FAILURE) {
					Exceptions.throwRuntime(r.getErrorMessage());
				}

			} else if (Platform.getState() != State.STARTING) {
				
				// Transfer only application-specific classes to the existing delegate
				// Note: other classes must have been registered when the delegate was
				// instantiated

				String appId = ClassLoaders.getId(cl);

				List<Class<?>> classes = SPILocatorHandlerImpl.getSpiClasses().get(type).get(appId);

				if (classes != null && !classes.isEmpty()) {

					// We need to first check if all classes can be ingested into the delegate in
					// place, if not, then a platform restart is required

					for (Class<?> clazz : classes) {

						if (!delegate.canRegisterInplace0(clazz)) {

							// Indicate that the platform needs restart
							return true;
						}
					}

					// Then, we need to sort the classes, if required by the delegate

					Comparator<?> comparator = delegate.getClassComparator();

					if (comparator != null) {
						classes.sort((Comparator<? super Class<?>>) comparator);
					}

					Collection<?> l = delegate.add0(classes);

					if (l.size() > 0) {
						Logger.get().warn("Delegate: " + delegate.getClass().getName() + " could not add resources: \n"
								+ l.stream().map(c -> ((Class<?>) c).getName()).collect(Collectors.toList()));
					}

				}
			}
		}

		return false;
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

		if (!isTrusted && delegate.getType().getKey().classification().requiresTrustedDelegate()) {
			throw new SecurityException("Only trusted Spi delegates can be used for an active Spi Type");
		}

		return delegate.init();
	}

	private static boolean isTrusted(SpiDelegate<?> delegate) {

		@SuppressWarnings("unchecked")
		Class<? extends SpiDelegate<?>> delegateClass = (Class<? extends SpiDelegate<?>>) delegate.getClass();

		return

		/**
		 * This is a platform-internal class
		 */
		ClassLoaders.getId(delegateClass).equals(AppProvisioner.DEFAULT_APP_ID) ||

		/**
		 * This class belongs to a trusted application
		 */
				SpiBaseImpl.isAppTrusted(ClassLoaders.getId(delegateClass));
	}

	@Override
	public Map<SpiType, SpiDelegate<?>> getDelegates() {
		return delegates;
	}

	@Override
	public DelegateResorceSet getResources(SpiType type) {
		return resources.get(type);
	}

	@Override
	public void releaseResources(SpiType type) {

		DelegateResorceSet rSet = resources.remove(type);

		rSet.getLocalStore().clear();

		Map<String, AsyncDistributedMap<String, Object>> stores = rSet.getDistributedStores();

		if (stores != null) {

			List<CompletableFuture<?>> futures = new ArrayList<>(stores.size());

			stores.forEach((k, v) -> {

				LOG.info("Clearing distributed cache: " + k);
				futures.add(v.clear());
			});

			LOG.info("Waiting for all caches to be cleared");
			CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
		}
	}

	@SuppressWarnings("unchecked")
	@BlockerBlockerTodo("Ensure that the SPIs are tranversed in the correct order")
	@BlockerTodo("In scenarios where a delegate for a given type is replaced in SpiBase, how is this handled")

	@SecureMethod(factor = Factor.CALLER, identityStrategy = IdentityStrategy.SAME, allowed = { SpiDelegate.class })
	public final <T> ResourcesInitResult addResources(SpiType type, Function<Class<T>, ResourceStatus> consumer) {

		Integer loadedClasses = 0;
		ResourcesInitResult result = null;

		// to do
		// Maintain a loop count, and ensure that the delegate's resource map has exact
		// size

		Map<String, List<Class<?>>> allClasses = SPILocatorHandlerImpl.getSpiClasses().get(type);

		loop: for (List<Class<?>> l : allClasses.values()) {
			for (Class<?> c : l) {

				Class<T> clazz = (Class<T>) c;

				ResourceStatus b = consumer.apply(clazz);

				if (b == ResourceStatus.ERROR) {

					result = result != null ? result : ResourcesInitResult.PARTIAL_SUCCESS;
					result.addError((Class<? extends AbstractResource>) c, b.getMessage());

				} else {

					// Note that at this point, ResourceStatus can either be UPDATED or NOT_UPDATED
					// both
					// of which acknowledge ingestion of the resource into the system

					loadedClasses++;
				}

				if (type.getCount() != -1 && loadedClasses == type.getCount()) {

					// In some cases - though not very common, a specific spiType may require only a
					// specific number
					// of resource classes to be loaded, in which case we select
					LOG.debug("All resources has been added successfully for SpiType: " + type.name() + " ..");
					break loop;
				}
			}
		}

		return (result != null ? result : ResourcesInitResult.SUCCESS).setCount(loadedClasses);
	}
}
