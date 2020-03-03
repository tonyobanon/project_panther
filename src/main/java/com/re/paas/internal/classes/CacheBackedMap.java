package com.re.paas.internal.classes;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.infinispan.Cache;

import com.re.paas.api.classes.AsyncDistributedMap;

public class CacheBackedMap implements AsyncDistributedMap<String, Object> {

	private final Cache<String, Object> cache;
	
	public CacheBackedMap(Cache<String, Object> cache) {
		this.cache = cache;
	}

	@Override
	public CompletableFuture<Integer> size() {
		return this.cache.sizeAsync().thenApply(r -> r.intValue());
	}

	@Override
	public CompletableFuture<Boolean> isEmpty() {
		return CompletableFuture.completedFuture(this.cache.isEmpty());
	}

	@Override
	public CompletableFuture<Boolean> containsKey(Object key) {
		return this.cache.containsKeyAsync((String) key);
	}

	@Override
	public CompletableFuture<Boolean> containsValue(Object value) {
		return CompletableFuture.completedFuture(this.cache.containsValue(value));
	}

	@Override
	public CompletableFuture<Object> get(Object key) {
		return this.cache.getAsync((String) key);
	}

	@Override
	public CompletableFuture<Object> put(String key, Object value) {
		return this.cache.putAsync(key, value);
	}

	@Override
	public CompletableFuture<Object> remove(Object key) {
		return this.cache.removeAsync(key);
	}

	@Override
	public CompletableFuture<Boolean> remove(String key, Object value) {
		return this.cache.removeAsync(key, value);
	}
	
	@Override
	public CompletableFuture<?> putAll(Map<? extends String, ? extends Object> m) {
		return this.cache.putAllAsync(m);
	}

	@Override
	public CompletableFuture<?> clear() {
		return this.cache.clearAsync();
	}

	@Override
	public CompletableFuture<Set<String>> keySet() {
		return this.keySet();
	}

	@Override
	public CompletableFuture<Collection<Object>> values() {
		return CompletableFuture.completedFuture(this.cache.values());
	}

	@Override
	public CompletableFuture<Set<Entry<String, Object>>> entrySet() {
		return CompletableFuture.completedFuture(this.cache.entrySet());
	}

	@Override
	public Map<String, Object> toSyncMap() {
		return new SyncDistributedMap<>(this);
	}

}
