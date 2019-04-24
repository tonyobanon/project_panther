package com.re.paas.internal.models.listables;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.listable.AbstractIndexedNameTypeDelegate;
import com.re.paas.api.listable.AbstractListableDelegate;
import com.re.paas.api.listable.IndexedNameType;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.runtime.spi.DelegateInitResult;

@BlockerTodo("Make good use of the resource maps, where namepaces are stored")
public class IndexedNameTypeDelegate extends AbstractIndexedNameTypeDelegate {

	private static final String INDEXED_NAME_TYPE_DELIMETER = "_";
	private static Map<String, IndexedNameType> indexedNameTypes = new HashMap<>();

	@Override
	public DelegateInitResult init() {
		forEach(this::addIndexedNameType);
		return DelegateInitResult.SUCCESS;
	}

	@Override
	protected void add(List<Class<IndexedNameType>> classes) {
		classes.forEach(this::addIndexedNameType);
	}

	@Override
	protected List<Class<IndexedNameType>> remove(List<Class<IndexedNameType>> classes) {
		classes.forEach(this::removeIndexedNameType);
		return Collections.emptyList();
	}

	private void addIndexedNameType(Class<IndexedNameType> c) {

		IndexedNameType[] nameTypes = c.getEnumConstants();

		if (nameTypes.length == 0) {
			return;
		}

		String namespace = nameTypes[0].namespace();

		// Ensure that all constants have the same namespace
		for (IndexedNameType f : nameTypes) {
			if (!f.namespace().equals(namespace)) {
				throw new RuntimeException("All constants in " + c.getName() + " should have the same namespace");
			}
		}

		Map<Integer, IndexedNameType> nameTypesMap = new HashMap<>(nameTypes.length);

		for (IndexedNameType f : nameTypes) {
			nameTypesMap.put(f.id(), f);
		}

		@SuppressWarnings("unchecked")
		Map<Integer, IndexedNameType> existingMap = (Map<Integer, IndexedNameType>) get(namespace);

		if (existingMap == null) {
			set(namespace, nameTypesMap);
		} else {

			nameTypesMap.forEach((k, v) -> {
				if (existingMap.containsKey(k)) {
					throw new RuntimeException(
							"Namespace: " + namespace + " already contains id: " + k + " => " + c.getName());
				}
				existingMap.put(k, v);
			});
		}

		for (IndexedNameType f : nameTypes) {
			indexedNameTypes.put(toString(f), f);
		}
	}

	private void removeIndexedNameType(Class<IndexedNameType> c) {

		IndexedNameType[] nameTypes = c.getEnumConstants();

		if (nameTypes.length > 0) {
			return;
		}

		
		// Ensure that no listable uses this any of the name types
		
		AbstractListableDelegate listableDelegate = Listable.getDelegate();
		
		for (IndexedNameType f : nameTypes) {
			if (listableDelegate.getListable(f) != null) {
				throw new RuntimeException("The IndexedNameType: " + toString(f) + " is still in use by " + listableDelegate.getClass().getName());
			}
		}

		String namespace = nameTypes[0].namespace();

		@SuppressWarnings("unchecked")
		Map<Integer, IndexedNameType> existingMap = (Map<Integer, IndexedNameType>) get(namespace);

		for (IndexedNameType f : nameTypes) {
			existingMap.remove(f.id());
		}

		if (existingMap.isEmpty()) {
			remove(namespace);
		}

		for (IndexedNameType f : nameTypes) {
			indexedNameTypes.remove(toString(f));
		}
	}

	@Override
	public String toString(IndexedNameType type) {
		return type.namespace() + INDEXED_NAME_TYPE_DELIMETER + type.id();
	}

	@Override
	public IndexedNameType fromString(String typeString) {
		return indexedNameTypes.get(typeString);
	}

}
