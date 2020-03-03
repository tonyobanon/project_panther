package com.re.paas.internal.classes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.re.paas.api.classes.AsyncDistributedMap;

public class SyncDistributedMap<K, V> implements Map<K, V> {

	private final AsyncDistributedMap<K, V> map;
	
	public SyncDistributedMap(AsyncDistributedMap<K, V> map) {
		this.map = map;
	}
	
	@Override
	public int size() {
		return this.map.size().join();
	}

	@Override
	public boolean isEmpty() {
		return this.map.isEmpty().join();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.map.containsKey(key).join();
	}

	@Override
	public boolean containsValue(Object value) {
		return this.map.containsValue(value).join();
	}

	@Override
	public V get(Object key) {
		return this.map.get(key).join();
	}

	@Override
	public V put(K key, V value) {
		return this.map.put(key, value).join();
	}

	@Override
	public V remove(Object key) {
		return this.map.remove(key).join();
	}
	
	public Boolean remove(String key, Object value) {
		return this.map.remove(key, value).join();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.map.putAll(m).join();
	}

	@Override
	public void clear() {
		this.map.clear().join();
	}

	@Override
	public Set<K> keySet() {
		return this.map.keySet().join();
	}

	@Override
	public Collection<V> values() {
		return this.map.values().join();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return this.map.entrySet().join();
	}
	
	public AsyncDistributedMap<K, V> getAsyncMap() {
		return map;
	}

}
