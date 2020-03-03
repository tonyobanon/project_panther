package com.re.paas.api.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.re.paas.api.collections.MapEntryImpl;

public class Collections {

	public static <K, V> V firstValue(Map<K, V> map) {
		for (V v : map.values()) {
			return v;
		}
		return null;
	}

	public static <V> V firstValue(Collection<V> col) {
		Iterator<V> it = col.iterator();
		while (it.hasNext()) {
			return it.next();
		}
		return null;
	}

	public static <K, V> Entry<K, V> asEntry(K key, V value) {
		return new MapEntryImpl<K, V>(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public static <V> Set<V> asSet(Collection<Object> col) {
		HashSet<V> s = new HashSet<>(col.size());
		col.forEach(v -> s.add((V) v));
		return s;
	}
}
