package com.re.paas.api.fusion;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Session<K, V> {

	String id();
	
	RoutingContext context();
	
	CompletableFuture<V> get(K k);
	
	CompletableFuture<V> put(K k, V v);
	
	CompletableFuture<Integer> delete(K k);
	
	CompletableFuture<Map<K, V>> data();
	
}
