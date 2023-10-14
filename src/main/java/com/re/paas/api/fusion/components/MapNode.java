package com.re.paas.api.fusion.components;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


public final class MapNode<K, V> extends AbstractMap<K, V> implements VectorNode<Map<K, V>> {

	private VectorNode<?> parent;

	MapNode(VectorNode<?> parent, Map<? extends K, ? extends V> coll) {
		this.parent = parent;

		// TODO Add coll
	}

	@Override
	public VectorNode<?> getParent() {
		return this.parent;
	}

	void setParent(VectorNode<?> parent) {
		this.parent = parent;
	}


	@Override
	public Set<Entry<K, V>> entrySet() {
		
		// REMOVE
//		return children.entrySet().stream()
//				.map(e -> new MapEntryImpl<K, V>(e.getKey(), NodeUtil.getPojoFromNode(e.getValue().getValue())))
//				.collect(Collectors.toSet());
		
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V put(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

}
