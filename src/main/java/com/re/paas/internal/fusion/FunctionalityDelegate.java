package com.re.paas.internal.fusion;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.fusion.functionalities.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourceStatus;

@BlockerTodo("Make good use of the resource maps, where namepaces are stored")
public class FunctionalityDelegate extends AbstractFunctionalityDelegate {

	private static Map<String, Functionality> functionalities = new HashMap<>();

	@Override
	public DelegateInitResult init() {
		return forEach(this::add0);
	}

	@Override
	protected ResourceStatus add(Class<Functionality> clazz) {
		return add0(clazz);
	}

	private ResourceStatus add0(Class<Functionality> clazz) {
		int count = addFunctionality(clazz);
		return count > 0 ? ResourceStatus.UPDATED.setCount(count) : ResourceStatus.NOT_UPDATED;
	}

	@Override
	protected ResourceStatus remove(Class<Functionality> clazz) {
		int count = removeFunctionality(clazz);
		return count > 0 ? ResourceStatus.UPDATED.setCount(count) : ResourceStatus.NOT_UPDATED;
	}

	@Override
	protected Collection<?> getResourceObjects() {
		return functionalities.values();
	}

	@Override
	public Functionality fromString(String functionalityString) {
		return functionalities.get(functionalityString);
	}

	@Override
	public Functionality getFunctionality(String namespace, Integer id) {
		return functionalities.get(toString(namespace, id));
	}

	private Integer addFunctionality(Class<Functionality> c) {

		Functionality[] functionalities = c.getEnumConstants();

		if (functionalities.length == 0) {
			return 0;
		}

		String namespace = functionalities[0].namespace();

		// Ensure that all constants have the same namespace
		for (Functionality f : functionalities) {
			if (!f.namespace().equals(namespace)) {
				throw new RuntimeException("All constants in " + c.getName() + " should have the same namespace");
			}
		}

		Map<Integer, Functionality> functionalitiesMap = new HashMap<>(functionalities.length);

		for (Functionality f : functionalities) {
			functionalitiesMap.put(f.id(), f);
		}

		@SuppressWarnings("unchecked")
		Map<Integer, Functionality> existingMap = (Map<Integer, Functionality>) getLocalStore().get(namespace);

		if (existingMap == null) {
			getLocalStore().put(namespace, functionalitiesMap);
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

		return functionalities.length;
	}

	private Integer removeFunctionality(Class<Functionality> c) {

		Functionality[] functionalities = c.getEnumConstants();

		if (functionalities.length == 0) {
			return 0;
		}

		String namespace = functionalities[0].namespace();

		@SuppressWarnings("unchecked")
		Map<Integer, Functionality> existingMap = (Map<Integer, Functionality>) getLocalStore().get(namespace);

		for (Functionality f : functionalities) {
			existingMap.remove(f.id());
		}

		if (existingMap.isEmpty()) {
			getLocalStore().remove(namespace);
		}

		for (Functionality f : functionalities) {
			FunctionalityDelegate.functionalities.remove(toString(f));
		}

		return functionalities.length;
	}

	@Override
	public Collection<Functionality> all() {
		return functionalities.values();
	}

}
