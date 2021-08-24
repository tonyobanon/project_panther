package com.re.paas.api.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.re.paas.api.classes.MapEntryImpl;

import java.util.Set;

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
	
	public static <T> Set<T> findDuplicates(List<T> l) {
		 
		final Set<T> setToReturn = new HashSet<T>();
		final Set<T> set1 = new HashSet<T>();
 
		for (T yourInt : l) {
			if (!set1.add(yourInt)) {
				setToReturn.add(yourInt);
			}
		}
		return setToReturn;
	}
	
	public static <T> boolean hasUniqueElements(List<T> l) {
		return findDuplicates(l).isEmpty();
	}
}
