package com.re.paas.internal.infra.cache.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.classes.LongWrapper;
import com.re.paas.api.infra.cache.Cache;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.infra.cache.CacheFactoryStats;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;

@BlockerTodo("In this class, find alternative to HashMap and ArrayList for high performance")
public class RedisCacheFactory implements CacheFactory<String, Object> {

	private static final int defaultExponent = 5;
	private static final int clientCountCheckInterval = 5000;

	private static final String statsInternalHash = "_si";
	private static final String clientCountKey = "_cc";

	private int exponent = defaultExponent;

	private final Logger LOG;

	private final String name;

	private final List<String> used;
	private final List<String> free;

	private long clientCount = 0;
	private final RedisConfig config;

	private final Map<String, LongWrapper> instanceStackCount;
	private final Map<String, Cache<String, Object>> instancePool;

	private boolean isUpgradable = true;
	private ScheduledExecutorService upgradeScheduler;

	private Runnable shutdownFuture;
	private boolean isShutdown = false;

	public RedisCacheFactory(String name, RedisConfig config) {

		this.LOG = LoggerFactory.get().getLog().setNamespace(RedisCacheFactory.class, name);

		this.name = name;
		this.config = config;

		this.used = Collections.synchronizedList(new ArrayList<>());
		this.free = Collections.synchronizedList(new ArrayList<>());

		this.instanceStackCount = Collections.synchronizedMap(new HashMap<>());
		this.instancePool = Collections.synchronizedMap(new HashMap<>());

		this.upgradePool();
		this.scheduleUpgrade();
	}

	private void scheduleUpgrade() {

		upgradeScheduler = Executors.newScheduledThreadPool(1);

		upgradeScheduler.scheduleAtFixedRate(() -> {

			if (!isFactoryAvailable() || !isUpgradable) {
				
				exponent = -1;
				upgradeScheduler.shutdown();
				return;
			}

			clientCount = Long.parseLong(this.getInternal().hget(statsInternalHash, clientCountKey).join().toString());

			if (needsUpgrade()) {
				if (canUpgrade()) {
					this.upgradePool();
				}
			}

		}, 0, clientCountCheckInterval, TimeUnit.MILLISECONDS);
	}

	RedisConfig getConfig() {
		return this.config;
	}

	void incrStack(String instanceId) {

		instanceStackCount.get(instanceId).add();

		Long current = instanceStackCount.get(instanceId).get();

		LOG.debug("incrStack: " + current);

		int index = Collections.binarySearch(free, instanceId, null);

		if (index > 0) {

			LOG.debug("index of " + instanceId + " = " + index + ", marking as used");

			// Mark client as 'used'
			free.remove(index);
			used.add(instanceId);
		}
	}

	void decrStack(String instanceId) {

		instanceStackCount.get(instanceId).minus();

		Long current = instanceStackCount.get(instanceId).get();

		LOG.debug("decrStack: " + current);

		if (current.equals(0L)) {

			int index = Collections.binarySearch(used, instanceId, null);

			if (index >= 0) {
				// In the case of the default internal instance (free[0]), this may be -1

				LOG.debug("index of " + instanceId + " = " + index + ", marking as free");

				// Mark client as 'free'
				used.remove(index);
				free.add(instanceId);
			}
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Cache<String, Object> getInternal() {

		if (!isFactoryAvailable()) {
			return null;
		}

		String instanceId = free.get(0);

		LOG.debug("Using internal instance: " + instanceId);

		return instancePool.get(instanceId);
	}

	/**
	 * This is typically called in scenarios where the redis pool cannot be upgraded
	 * with a call to get(). An instance that is currently being used is therefore
	 * returned
	 */
	@Override
	public Cache<String, Object> getAny() {

		if (!isFactoryAvailable()) {
			return null;
		}

		int index = new Random().nextInt(used.size() - 1);

		if (index == 0) {
			return getAny();
		}

		String instanceId = used.get(index);

		LOG.debug("Using available instance: " + instanceId);

		return instancePool.get(instanceId);
	}

	@Override
	public Cache<String, Object> get() {

		if (!isFactoryAvailable()) {
			return null;
		}

		if (free.size() < 2) {
			return getAny();
		}

		String instanceId = free.get(free.size() - 1);

		LOG.debug("Using free instance: " + instanceId);

		return instancePool.get(instanceId);
	}

	@Override
	public void shutdown() {

		if (shutdownFuture != null) {
			return;
		}

		shutdownFuture = () -> {

			LOG.debug("Shutting down cache pool..");

			Long len = (long) (used.size() + free.size());
			getInternal().hincrby(statsInternalHash, clientCountKey, (len * -1)).join();

			List<String> instances = new ArrayList<>(len.intValue());
			instances.addAll(free);
			instances.addAll(used);

			free.clear();
			used.clear();

			instances.forEach(id -> {

				instancePool.get(id).quit();
			});

			instanceStackCount.clear();
			instancePool.clear();

			isUpgradable = false;
			clientCount = clientCount - len.intValue();

			shutdownFuture = null;
			isShutdown = true;
		};

		shutdownFuture.run();
	}

	@Override
	public boolean isShutdown() {
		return isShutdown;
	}

	/**
	 * 
	 * This registers a new instance in this pool
	 * 
	 * @param cache Cache object to register
	 */
	private void addInstance(RedisCache cache) {

		instanceStackCount.put(cache.getInstanceId(), new LongWrapper(0L));
		instancePool.put(cache.getInstanceId(), cache);

		free.add(cache.getInstanceId());
	}

	public void upgradePool() {

		long instanceCount = exponent;

		LOG.debug("Adding " + instanceCount + " instance(s) to the pool");

		for (int i = 0; i < instanceCount; i++) {
			addInstance(new RedisCache(this));
		}

		getInternal().hincrby(statsInternalHash, clientCountKey, instanceCount);

		this.exponent += exponent;
	}

	@BlockerTodo("Implement this")
	public boolean downgradePool() {
		return false;
	}

	@Override
	public CacheFactoryStats getStatistics() {
		return new CacheFactoryStats().setExponent(exponent).setClientCount(clientCount).setTotalFree(free.size()).setTotalUsed(used.size())
				.setMaxConnectionsAllowed(getMaxConnectionAllowed()).setMaxConnections(config.getMaxConnections());
	}

	private long getMaxConnectionAllowed() {
		return config.getMaxConnections() - (int) (0.01 * config.getMaxConnections());
	}

	private boolean needsUpgrade() {
		return free.size() <= (int) 0.05 * exponent;
	}

	private boolean canUpgrade() {

		if (!isUpgradable) {
			return false;
		}

		if (clientCount >= getMaxConnectionAllowed()) {
			isUpgradable = false;
			return false;
		}

		boolean isUpgradable = this.isUpgradable;
		int exponent = this.exponent;

		for (; exponent > 0; exponent = (int) (exponent / 1.5)) {

			if (clientCount + exponent <= getMaxConnectionAllowed()) {
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

	@Override
	public boolean isUpgradable() {
		return isUpgradable;
	}

}