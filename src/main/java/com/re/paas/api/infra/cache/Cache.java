package com.re.paas.api.infra.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface Cache<K, V> {
	
	static Cache<String, Object> get() {
		return CacheAdapter.getDelegate().getCacheFactory().getAny();
	}

	void reset();

	void limit(int limit);

	void greedy();

	CompletableFuture<PartitionedSet<String>> sget(K key, Checkpoint checkpoint);

	CompletableFuture<PartitionedSet<String>> sgetOrDefault(PersistenceType persistenceType, K key, Checkpoint checkpoint,
			Callable<List<String>> producer);

	CompletableFuture<Boolean> sadd(PersistenceType persistenceType, K key, List<String> elements);

	CompletableFuture<Boolean> sdel(K key, String value);

	CompletableFuture<Boolean> mset(PersistenceType persistenceType, K key, Map<K, V> value);
	
	CompletableFuture<V> get(K key);
	
	CompletableFuture<String> set(K key, V value);
	
	@SuppressWarnings("unchecked")
	CompletableFuture<Long> del(K... keys);
	
	CompletableFuture<Boolean> hset(K key, K field, V value);
	
	CompletableFuture<V> hget(K key, K field);
	
	CompletableFuture<Map<K, V>> hgetall(K key);
	
	@SuppressWarnings("unchecked")
	CompletableFuture<Long> hdel(K key, K... fields);
	
	CompletableFuture<List<K>> hkeys(K key);
	
	CompletableFuture<Long> hincrby(K key, K field, Long amount);
	
	CompletableFuture<Long> hlen(K key);
	
	CompletableFuture<Boolean> expire(K key, Long seconds);
	
	CompletableFuture<Boolean> expire(String key, PersistenceType persistenceType);
	
	CompletableFuture<Boolean> quit();
}
