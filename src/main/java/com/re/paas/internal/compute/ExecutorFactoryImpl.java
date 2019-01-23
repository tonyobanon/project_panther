package com.re.paas.internal.compute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.LongWrapper;
import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.concurrency.ExecutorFactoryStats;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.infra.cache.CacheFactoryStats;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.threadsecurity.ThreadSecurity;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.clustering.MasterNodeRole;

@BlockerTodo("In this class, find alternative to HashMap and ArrayList for high performance")
@EventListener
public class ExecutorFactoryImpl {

	private static final int defaultExponent = 5;
	private static final int executorCountCheckInterval = 5000;

	private int exponent = defaultExponent;

	private final Logger LOG;

	private final String name;

	private final List<String> used;
	private final List<String> free;

	private final ExecutorFactoryConfig config;

	private final Map<String, ExecutorService> executorPool;

	private boolean isUpgradable = true;
	private ScheduledExecutorService upgradeScheduler;

	private Runnable shutdownFuture;
	private boolean isShutdown = false;

	public ExecutorFactoryImpl(String name, ExecutorFactoryConfig config) {

		this.LOG = LoggerFactory.get().getLog().setNamespace(ExecutorFactoryImpl.class, name);

		this.name = name;
		this.config = config;

		this.used = Collections.synchronizedList(new ArrayList<>());
		this.free = Collections.synchronizedList(new ArrayList<>());

		this.executorPool = Collections.synchronizedMap(new HashMap<>());

		this.upgradePool();
		this.scheduleUpgrade();
	}

	@Subscribe
	public void onComputeQuotaExceeded(ComputeQuotaExceededEvent evt) {

		// This is triggered, when this pool can no longer be upgraded
		// We contact the master, requesting for a new node to be provisioned

		// MasterNodeRole.getDelegate().getMasterRole().
	}

	private void scheduleUpgrade() {

		upgradeScheduler = Executors.newScheduledThreadPool(1);

		upgradeScheduler.scheduleAtFixedRate(() -> {

			if (!isFactoryAvailable() || !isUpgradable) {

				exponent = -1;
				upgradeScheduler.shutdown();
				return;
			}

			if (needsUpgrade()) {

				if (canUpgrade()) {
					this.upgradePool();
				}

				if (!isUpgradable) {

					ComputeQuotaExceededEvent evt = new ComputeQuotaExceededEvent();
					AbstractEventDelegate.getInstance().dispatch(evt, true);
				}

			}

		}, 0, executorCountCheckInterval, TimeUnit.MILLISECONDS);
	}

	ExecutorFactoryConfig getConfig() {
		return this.config;
	}

	public String getName() {
		return this.name;
	}

	public Future<?> execute(String appId, Runnable task) {

		if (free.isEmpty()) {
			Exceptions.throwRuntime(new ComputeException("No executor(s) available to run this task"));
		}

		// Get available instance

		String executorId = free.get(free.size() - 1);
		ExecutorService executor = executorPool.get(executorId);

		markAsUsed(executorId);

		executor.submit(() -> {

		});

		// set context classloader
		// set permissions

		free.remove(o);

		return null;
	}

	private void markAsFree(String id) {
		this.free.add(id);
		this.used.remove(id);
	}

	private void markAsUsed(String id) {
		this.free.remove(id);
		this.used.add(id);
	}

	public void shutdown() {

		if (shutdownFuture != null) {
			return;
		}

		shutdownFuture = () -> {

			LOG.debug("Shutting down executor pool..");

			Long len = (long) (used.size() + free.size());

			List<String> instances = new ArrayList<>(len.intValue());
			instances.addAll(free);
			instances.addAll(used);

			free.clear();
			used.clear();

			instances.forEach(id -> {
				executorPool.get(id).shutdown();
			});

			executorPool.clear();

			isUpgradable = false;

			shutdownFuture = null;
			isShutdown = true;
		};

		shutdownFuture.run();
	}

	public boolean isShutdown() {
		return isShutdown;
	}

	/**
	 * This registers a new instance in this pool
	 */
	private void addInstance() {

		String id = Utils.newShortRandom();

		ExecutorService executor = Executors.newSingleThreadExecutor();

		if (!config.isTrusted()) {
			try {

				executor.submit(() -> {

					// untrust

				}).get();

			} catch (InterruptedException | ExecutionException e) {
				Exceptions.throwRuntime(e);
			}
		}

		executorPool.put(id, executor);
		free.add(id);
	}

	public void upgradePool() {

		long executorCount = exponent;

		LOG.debug("Adding " + executorCount + " executors(s) to the pool");

		for (int i = 0; i < executorCount; i++) {
			addInstance();
		}

		this.exponent += exponent;
	}

	@BlockerTodo("Implement this")
	public boolean downgradePool() {
		return false;
	}

	public ExecutorFactoryStats getStatistics() {
		return new ExecutorFactoryStats().setExponent(exponent).setTotalFree(free.size()).setTotalUsed(used.size())
				.setMaxThreads(config.getMaxThreads()).setThreadCount(executorPool.size());
	}

	private long getMaxExecutorsAllowed() {
		return config.getMaxThreads();
	}

	private boolean needsUpgrade() {
		return free.size() <= (int) 0.05 * exponent;
	}

	private boolean canUpgrade() {

		if (!isUpgradable) {
			return false;
		}

		int executorSize = executorPool.size();

		if (executorSize >= getMaxExecutorsAllowed()) {
			isUpgradable = false;
			return false;
		}

		boolean isUpgradable = this.isUpgradable;
		int exponent = this.exponent;

		for (; exponent > 0; exponent = (int) (exponent / 1.5)) {

			if (executorSize + exponent + 1 <= getMaxExecutorsAllowed()) {
				break;
			} else {
				isUpgradable = false;
			}
		}

		this.exponent = exponent;
		this.isUpgradable = isUpgradable;

		return exponent >= 1;
	}

	private boolean isFactoryAvailable() {
		return shutdownFuture == null && !isShutdown;
	}

	public boolean isUpgradable() {
		return isUpgradable;
	}

}