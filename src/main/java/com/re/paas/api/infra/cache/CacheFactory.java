package com.re.paas.api.infra.cache;

import java.util.List;

import com.re.paas.api.runtime.spi.ShutdownPhase;

public interface CacheFactory<K, V> {

	String getName();
	
	CacheAdapter getAdapter();

	Cache<K, V> get(String bucket);

	/**
	 * This returns a list of buckets, for which cache instance(s) have been
	 * created, accross all cache factory instance(s)
	 * 
	 * @return
	 */
	List<String> bucketList();
	
	default void initialize() {
	}
	
	/**
	 * The factory is responsible for releasing resources being used by it's adapter
	 */
	default void shutdown(ShutdownPhase phase) {
	}
}
