package com.re.paas.api.infra.cache;

public interface CacheFactory<K, V> {

	String getName();
	
	Cache<K, V> getInternal();
	
	Cache<K, V> getAny();
	
	Cache<K, V> get();
	
	void shutdown();
	
	boolean isShutdown();
	
	boolean isUpgradable();
	
	CacheFactoryStats getStatistics();
}
