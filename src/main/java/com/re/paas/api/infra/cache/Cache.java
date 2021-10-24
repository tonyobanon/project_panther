package com.re.paas.api.infra.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Cache<K, V> {

	static Cache<String, Object> get(String bucket) {
		return CacheAdapter.getDelegate().getCacheFactory().get(bucket);
	}

	/* GENERIC */

	CompletableFuture<Boolean> exists(K key);

	CompletableFuture<V> get(K key);

	CompletableFuture<V> set(K key, V value);
	
	CompletableFuture<Object> setex(String key, Object value, Long lifespan);
	
	CompletableFuture<V> set(K key, V value, Long maxIdle);
	
	CompletableFuture<Integer> del(K key);
	
	CompletableFuture<Integer> del(@SuppressWarnings("unchecked") K... keys);

	CompletableFuture<Integer> incrby(K key, Integer amount);

	
	CompletableFuture<Boolean> expireInSecs(K key, Long seconds);
	
	CompletableFuture<Boolean> invalidateInSecs(K key, Long seconds);
	
	CompletableFuture<CacheEntryType> type(K key);


	/* SETS */

	CompletableFuture<Integer> slength(K key);

	CompletableFuture<?> sget(K key, Function<V, CompletableFuture<?>> consumer);

	CompletableFuture<Integer> sadd(K key, List<V> elements);

	CompletableFuture<Integer> sdel(K key, V value);
	
	CompletableFuture<Integer> sdel(K key, @SuppressWarnings("unchecked") V... values);

	/* HASHES */

	CompletableFuture<V> hset(K key, K field, V value);

	CompletableFuture<V> hget(K key, K field);

	CompletableFuture<Map<K, V>> hgetall(K key);
	
	CompletableFuture<Integer> hdel(K key, K field);

	CompletableFuture<Integer> hdel(K key, @SuppressWarnings("unchecked") K... fields);

	CompletableFuture<?> hkeys(K key, Function<String, CompletableFuture<?>> consumer);

	CompletableFuture<List<K>> hkeys(K key);

	CompletableFuture<Integer> hincrby(K key, K field, Integer amount);

	CompletableFuture<Integer> hlen(K key);

}
