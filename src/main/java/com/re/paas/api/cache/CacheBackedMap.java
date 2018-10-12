package com.re.paas.api.cache;

import java.util.AbstractMap;
import java.util.Set;

public class CacheBackedMap<K, V> extends AbstractMap<K, V> {

	private static final PersistenceType defaultPersistenceType = PersistenceType.LONG_LIVED;
	
	public CacheBackedMap() {
		this(defaultPersistenceType);
	}
	
	/**
	 * 
	 * @param evictionThreshold Idle entries will remain in cache until this threshold elapses
	 */
	public CacheBackedMap(PersistenceType evictionThreshold) {
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		return null;
	}
	
	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return super.remove(key);
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		// TODO Auto-generated method stub
		return super.remove(key, value);
	}
	
	@Override
	public V get(Object key) {
		// TODO Auto-generated method stub
		return super.get(key);
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		super.clear();
	}

}
