package com.re.paas.api.fusion;

import java.util.Date;
import java.util.Map;

public interface Session<K, V> {

	String id();
	
	RoutingContext context();
	
	V get(K k);
	
	void put(K k, V v);
	
	void delete(K k);
	
	Map<K, V> data();
	
	Date dateCreated();
	
	Date dateUpdated();
	
}
