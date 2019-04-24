package com.re.paas.internal.fusion.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.runtime.spi.DelegateInitResult;

@BlockerTodo("Make good use of the resource maps, where namepaces are stored")
public class FunctionalityDelegate extends AbstractFunctionalityDelegate {

	private static Map<String, Functionality> functionalities = new HashMap<>();
	
	@Override
	public DelegateInitResult init() {
		forEach(this::addFunctionality);
		return DelegateInitResult.SUCCESS;
	}

	@Override
	protected void add(List<Class<Functionality>> classes) {
		classes.forEach(this::addFunctionality);
	}

	@Override
	protected List<Class<Functionality>> remove(List<Class<Functionality>> classes) {
		classes.forEach(this::removeFunctionality);
		return Collections.emptyList();
	}

	@Override
	public Functionality fromString(String functionalityString) {
		return functionalities.get(functionalityString);
	}

	@Override
	public Functionality getFunctionality(String namespace, Integer id) {
		return functionalities.get(toString(namespace, id));
	}

	private void addFunctionality(Class<Functionality> c) {

		Functionality[] functionalities = c.getEnumConstants();

		if (functionalities.length == 0) {
			return;
		}
		
		String namespace = functionalities[0].namespace();

		// Ensure that all constants have the same namespace
		for(Functionality f : functionalities) {
			if(!f.namespace().equals(namespace)) {
				throw new RuntimeException(
						"All constants in " + c.getName() + " should have the same namespace");
			}
		}
		
		Map<Integer, Functionality> functionalitiesMap = new HashMap<>(functionalities.length);

		for (Functionality f : functionalities) {
			functionalitiesMap.put(f.id(), f);
		}

		@SuppressWarnings("unchecked")
		Map<Integer, Functionality> existingMap = (Map<Integer, Functionality>) get(namespace);

		if (existingMap == null) {
			set(namespace, functionalitiesMap);
		} else {

			functionalitiesMap.forEach((k, v) -> {
				if (existingMap.containsKey(k)) {
					throw new RuntimeException(
							"Namespace: " + namespace + " already contains context-id: " + k + " => " + c.getName());
				}
				existingMap.put(k, v);
			});
		}

		for (Functionality f : functionalities) {
			FunctionalityDelegate.functionalities.put(toString(f), f);
		}
	}

	private void removeFunctionality(Class<Functionality> c) {

		Functionality[] functionalities = c.getEnumConstants();

		if (functionalities.length > 0) {
			return;
		}
		
		
		// Ensure that no realm uses this any of the functionalities
		
		AbstractRealmDelegate realmDelegate = Realm.getDelegate();
		Map<String, Collection<String>> allFunctionalities = realmDelegate.getAllFunctionalities();
		
		for(Functionality f : functionalities) {
			allFunctionalities.forEach((k,v) -> {
				if(v.contains(toString(f))) {
					throw new RuntimeException("The functionality: " + toString(f) + " is used by " + k);
				}
			});
		}
		

		String namespace = functionalities[0].namespace();

		@SuppressWarnings("unchecked")
		Map<Integer, Functionality> existingMap = (Map<Integer, Functionality>) get(namespace);

		for (Functionality f : functionalities) {
			existingMap.remove(f.id());
		}

		if (existingMap.isEmpty()) {
			remove(namespace);
		}

		for (Functionality f : functionalities) {
			FunctionalityDelegate.functionalities.remove(toString(f));
		}
	}
	
	@Override
	public Collection<Functionality> all() {
		return functionalities.values();
	}

}
