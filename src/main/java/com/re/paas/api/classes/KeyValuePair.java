package com.re.paas.api.classes;

import java.util.Map.Entry;

public class KeyValuePair<K, V> implements Entry<K, V> {

	private K key;
	private V value;

	public KeyValuePair() {
	}

	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public KeyValuePair<K, V> setKey(K key) {
		this.key = key;
		return this;
	}

	public V getValue() {
		return value;
	}

	public V setValue(V value) {
		this.value = value;
		return value;
	}
}
