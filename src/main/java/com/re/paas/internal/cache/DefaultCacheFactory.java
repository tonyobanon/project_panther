package com.re.paas.internal.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.cache.Cache;
import com.re.paas.api.cache.CacheFactory;
import com.re.paas.api.classes.LongWrapper;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.Utils;

/**
 * Maintains a pool of cache clients.
 * Note: DefaultCacheFactory.free[0] is reserved for internal use
 */
@BlockerTodo("Handle scenario when the pool is being shutdown. How are callers errored?")
public class DefaultCacheFactory implements CacheFactory<String, String> {

	private static final int defaultExponent = 5;
	private static final int clientCountCheckInterval = 10000;

	private static final String statsInternalPrefix = "_si";
	private static final String clientCountPrefix = "_cc";

	private static int exponent = defaultExponent;

	private static List<String> used = new ArrayList<>();
	private static List<String> free = new ArrayList<>();

	private static long clientCount = 0;
	private static final long maxClientCount = CacheConfig.get().getMaxConnections();
	private static final int maxClientCountOffset = (int) (0.01 * maxClientCount);

	private static final Map<String, LongWrapper> instanceStackCount = new HashMap<>();
	private static final Map<String, Cache<String, String>> instancePool = new HashMap<>();
	private static Runnable shutdownFuture;

	public static DefaultCacheFactory getInstance() {
		return new DefaultCacheFactory();
	}

	private static void log(String msg) {
		Logger.get().info(msg);
	}

	static void incrStack(String instanceId) {
		instanceStackCount.get(instanceId).add();

		Long current = instanceStackCount.get(instanceId).get();

		log("incrStack: " + current);

		int index = Collections.binarySearch(free, instanceId, null);

		if (index > 0) {

			log("index of " + instanceId + " = " + index);
			log("Marking as used");

			// Mark client as 'used'
			free.remove(index);
			used.add(instanceId);
		}
	}

	static void decrStack(String instanceId) {

		instanceStackCount.get(instanceId).minus();

		Long current = instanceStackCount.get(instanceId).get();

		log("decrStack: " + current);

		if (current.equals(0L)) {

			int index = Collections.binarySearch(used, instanceId, null);

			if (index >= 0) {
				// In the case of the default internal instance (free[0]), this may be -1

				log("index of " + instanceId + " = " + index);
				log("Marking as free");

				// Mark client as 'free'
				used.remove(index);
				free.add(instanceId);
			}
		}
	}

	@Override
	public Cache<String, String> getInternal() {
		String instanceId = free.get(0);
		// log('==> ' + instanceId);
		return instancePool.get(instanceId);
	}

	/**
	 * This is typically called in scenarios where the redis pool cannot be upgraded
	 * with a call to get(). An instance that is currently being used is therefore
	 * returned
	 */
	@Override
	public Cache<String, String> getAny() {

		int index = new Random().nextInt(used.size() - 1);
		String instanceId = used.get(index);
		log("==> " + instanceId);

		return instancePool.get(instanceId);
	}

	@Override
	public Cache<String, String> get() {

		if (free.size() <= 1) {
			return this.upgradePool() ? get() : getAny();
		}
		String instanceId = free.get(free.size() - 1);
		log("==> " + instanceId);

		return instancePool.get(instanceId);
	}

	@Override
	public void shutdown() {

		if (shutdownFuture != null) {
			return;
		}

		shutdownFuture = () -> {

			log("Shutting down cache pool..");

			Long len = (long) (used.size() + free.size());
			getInternal().hincrby(statsInternalPrefix, clientCountPrefix, (len * -1)).join();

			List<String> instances = new ArrayList<>(len.intValue());
			instances.addAll(free);
			instances.addAll(used);

			free.clear();
			used.clear();

			instances.forEach(id -> {
				instancePool.get(id).quit();
				instancePool.remove(id);
				log("deleted " + id);
			});

			exponent = defaultExponent;
			shutdownFuture = null;
		};

		shutdownFuture.run();
	}

	@Override
	public boolean upgradePool() {

		if (clientCount >= (maxClientCount - maxClientCountOffset)) {
			// No new instances can be added to this pool
			log("No new instances can be added to this pool. Re-using used instances");
			return false;
		}

		// Number of instances to create
		long instanceCount = exponent;

		log("Adding " + instanceCount + " instance(s) to the pool");

		for (int i = 0; i < instanceCount; i++) {

			String instanceId = Utils.newShortRandom();
			Cache<String, String> cache = new DefaultCache(instanceId);

			instanceStackCount.put(instanceId, new LongWrapper(0L));
			instancePool.put(instanceId, cache);

			free.add(instanceId);
			log("Created instance: " + instanceId);
		}

		getInternal().hincrby(statsInternalPrefix, clientCountPrefix, instanceCount);

		exponent += exponent;
		return true;
	}

	@Override
	public boolean downgradePool() {
		return false;
	}

	static {

		Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {

			DefaultCacheFactory factory = getInstance();

			if (free.isEmpty()) {
				factory.upgradePool();
			}

			clientCount = Long.parseLong(factory.getInternal().hget(statsInternalPrefix, clientCountPrefix).join());

		}, 0, clientCountCheckInterval, TimeUnit.MILLISECONDS);

	}

}
