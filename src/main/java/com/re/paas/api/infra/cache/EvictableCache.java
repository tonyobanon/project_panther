package com.re.paas.api.infra.cache;

import java.util.concurrent.CompletableFuture;

public interface EvictableCache<K, V> extends Cache<K, V> {

	CompletableFuture<V> set(K key, V value, Long maxIdle);
	
	CompletableFuture<Boolean> invalidateInSecs(K key, Long seconds);
	
}
