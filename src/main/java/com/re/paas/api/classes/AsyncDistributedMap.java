package com.re.paas.api.classes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public interface AsyncDistributedMap<K, V> {
	
	CompletableFuture<Integer> size();

	CompletableFuture<Boolean> isEmpty();

	CompletableFuture<Boolean> containsKey(Object key);

	CompletableFuture<Boolean> containsValue(Object value);

	CompletableFuture<V> get(Object key);

	CompletableFuture<V> put(K key, V value);

	CompletableFuture<V> remove(Object key);
	
	CompletableFuture<Boolean> remove(String key, Object value);

	CompletableFuture<?> putAll(Map<? extends K, ? extends V> m);

	CompletableFuture<?> clear();

	CompletableFuture<Set<K>> keySet();

	CompletableFuture<Collection<V>> values();

	CompletableFuture<Set<Entry<K, V>>> entrySet();
	
	Map<K, V> toSyncMap();

}