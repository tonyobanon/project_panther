package com.re.paas.api.cache;

public interface CacheFactory<K, V> {

	Cache<K, V> getInternal();
	
	Cache<K, V> getAny();
	
	Cache<K, V> get();
	
	void shutdown();
	
	boolean upgradePool();
	
	boolean downgradePool();
}
