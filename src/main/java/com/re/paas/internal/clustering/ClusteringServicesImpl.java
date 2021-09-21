package com.re.paas.internal.clustering;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.infinispan.commons.CacheConfigurationException;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.ConfigurationChildBuilder;
import org.infinispan.configuration.cache.StorageType;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.ClusterExecutor;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.multimap.api.embedded.EmbeddedMultimapCacheManagerFactory;
import org.infinispan.multimap.api.embedded.MultimapCache;
import org.infinispan.multimap.api.embedded.MultimapCacheManager;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Prototype;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.infra.cache.infinispan.InfinispanMashaller;

@Prototype
public class ClusteringServicesImpl implements ClusteringServices {

	private static final Logger LOG = LoggerFactory.get().getLog(ClusteringServicesImpl.class);

	private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	private static DefaultCacheManager cacheManager;

	private static final String genericMultiCacheKey = "generic-multi-cache";
	private static final String clusterWideTasksKey = "cluster-wide-tasks";

	private static boolean isExecutioner;

	// Set a reasonable initial capacity
	private static Map<String, CompletableFuture<?>> openTasks = new HashMap<String, CompletableFuture<?>>();

	public static ConfigurationChildBuilder getDefaultCacheConfiguration() {

		// Make the default cache a distributed synchronous one
		ConfigurationBuilder builder = new ConfigurationBuilder();

		return builder.clustering().cacheMode(CacheMode.SCATTERED_SYNC).memory().storage(StorageType.OFF_HEAP)
				.statistics().enable();
	}

	@Override
	public CompletableFuture<Void> start() throws IOException {
		return CompletableFuture.runAsync(() -> {
			try {
				start0();
			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}
		});
	}

	@BlockerTodo("Make the ConfigurationBuilder setings configurable individually in DistributedStoreConfig")
	private void start0() throws IOException {

		// throw new IOException("XYZ");

		// Setup up a clustered cache manager
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();

		// Add marshaller
		global.serialization().marshaller(new InfinispanMashaller());

		
		// Initialize the cache manager
		cacheManager = new DefaultCacheManager(global.build(), true);

		LOG.info("Joined infinispan cluster: %s", cacheManager.getClusterName());

		if (cacheManager.getClusterSize() == 1) {
			assumeExecutioner();
		}
	}

	public MultimapCacheManager<String, Object> getMultimapCacheManager() {

		// create or obtain a MultimapCacheManager passing the EmbeddedCacheManager

		@SuppressWarnings("unchecked")
		MultimapCacheManager<String, Object> multimapCacheManager = (MultimapCacheManager<String, Object>) EmbeddedMultimapCacheManagerFactory
				.from(cacheManager);

		return multimapCacheManager;
	}

	public DefaultCacheManager getCacheManager() {
		return getCacheManager0();
	}

	private static DefaultCacheManager getCacheManager0() {
		return cacheManager;
	}

	private MultimapCache<String, Object> getGenericMultimapCache() {

		MultimapCacheManager<String, Object> manager = getMultimapCacheManager();

		try {

			return manager.get(genericMultiCacheKey);

		} catch (CacheConfigurationException e) {

			if (e.getMessage().startsWith("ISPN000436")) {

				// cache configuration does not exist, create
				manager.defineConfiguration(genericMultiCacheKey, getDefaultCacheConfiguration().build());

				return manager.get(genericMultiCacheKey);
			}

			throw e;
		}
	}

	@Override
	public CompletableFuture<Collection<ClusterWideTask>> getClusterWideTasks() {
		return getGenericMultimapCache().get(clusterWideTasksKey)
				.thenApply(r -> r.stream().map(t -> (ClusterWideTask) t).collect(Collectors.toUnmodifiableList()));
	}

	@Override
	public void addClusterWideTask(ClusterWideTask task) {

		if (!isExecutioner()) {
			return;
		}

		getGenericMultimapCache().put(clusterWideTasksKey, task);

		registerTask(task);
	}

	private void registerTask(ClusterWideTask task) {

		if (task.getPredicate().getAsBoolean()) {

			Runnable r = task.getTask();

			if (task.getIntervalInSecs() == null) {
				r.run();
			} else {
				scheduledExecutor.scheduleAtFixedRate(r, task.getInitialDelay(), task.getIntervalInSecs(),
						TimeUnit.SECONDS);
			}
		}
	}

	@Override
	public void assumeExecutioner() {

		getClusterWideTasks().thenAccept(tasks -> {
			tasks.forEach(task -> {
				registerTask(task);
			});
		});

		DefaultCacheManager cm = getCacheManager0();

		LOG.debug("%s is now the executioner for cluster: %s", cm.getAddress(), cm.getClusterName());
		isExecutioner = true;
	}

	@Override
	public boolean isExecutioner() {
		return isExecutioner;
	}

	@Override
	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutor;
	}

	@Override
	public <T, R> CompletableFuture<R> execute(ParameterizedExecutable<T, R> task, boolean wait) {

		if (task.getAffinity() == Affinity.LOCAL) {
			return CompletableFuture.completedFuture(task.getFunction().apply(task.getParameter()));
		}
		
		boolean wait0 = wait || task.getAffinity() == Affinity.ALL;

		String taskId = Utils.newRandom();
		CompletableFuture<R> future = new CompletableFuture<R>();

		if (wait0) {
			openTasks.put(taskId, future);
		}

		ClusterExecutor executor = cacheManager.executor();
		String address = getCacheManager0().getAddress().toString();

		(task.getAffinity() == Affinity.ALL ? executor.allNodeSubmission() : executor.singleNodeSubmission())
				.execute(() -> {

					DefaultCacheManager cm = getCacheManager0();

					LOG.debug("Running task %s in %s from %s", taskId, cm.getAddress(), address);

					R r = task.getFunction().apply(task.getParameter());

					if (wait0) {
						cm.executor().filterTargets(a -> a.toString().equals(address)).execute(() -> {

							@SuppressWarnings("unchecked")
							CompletableFuture<R> f = (CompletableFuture<R>) ClusteringServicesImpl.openTasks
									.remove(taskId);

							f.complete(r);
						});
					}

				});

		if (!wait0) {
			future.complete(null);
		}

		return future;
	}
}
