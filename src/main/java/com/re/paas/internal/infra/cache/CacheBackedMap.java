package com.re.paas.internal.infra.cache;

import java.util.AbstractMap;
import java.util.Set;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.infra.cache.CacheFactory;
import com.re.paas.api.infra.cache.PersistenceType;

public class CacheBackedMap<K, V> extends AbstractMap<K, V> {

	private final CacheFactory<String, Object> cacheFactory;
	private final PersistenceType persistenceType;

	public CacheBackedMap(CacheFactory<String, Object> cacheFactory) {
		this(cacheFactory, PersistenceType.LONG_LIVED);
	}

	/**
	 * 
	 * <b> Implementation notes: </b> <br>
	 * 1. On each call to {@link CacheBackedMap#get(Object)}, also update the expiry
	 * of the entry
	 * <br>
	 * 2. When collections and Maps, are inserted, recursively create sets, and hashes in the backing
	 * 	  cache
	 * 
	 * @param evictionThreshold Idle entries will remain in cache until this
	 *                          threshold elapses
	 */
	@BlockerTodo
	public CacheBackedMap(CacheFactory<String, Object> cacheFactory, PersistenceType evictionThreshold) {
		this.cacheFactory = cacheFactory;
		this.persistenceType = evictionThreshold;	
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
	public V put(K key, V value) {
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		super.clear();
	}

}
