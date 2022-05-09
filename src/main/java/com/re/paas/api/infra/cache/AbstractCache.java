package com.re.paas.api.infra.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractCache<K, V> implements Cache<K, V> {

	CompletableFuture<List<V>> sget(K key) {
		var list = new ArrayList<V>();
		return this.sget(key, (v) -> {
			list.add(v);
			return CompletableFuture.completedFuture(null);
		}).thenApply(x -> list);
	}
	
	CompletableFuture<List<K>> hkeys(K key) {
		var list = new ArrayList<K>();
		return this.hkeys(key, (k) -> {
			list.add(k);
			return CompletableFuture.completedFuture(null);
		}).thenApply(x -> list);
	}
}
