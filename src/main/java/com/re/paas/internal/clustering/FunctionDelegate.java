package com.re.paas.internal.clustering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.clustering.AbstractFunctionDelegate;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.spi.DelegateInitResult;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.errors.ClusterFunctionError;

public class FunctionDelegate extends AbstractFunctionDelegate {

	private static Map<String, Short> functionIds = new HashMap<>();
	private static Map<Short, Function> functions = new HashMap<>();

	@Override
	public DelegateInitResult init() {
		Consumer<Class<Function>> consumer = c -> {
			addFunction(c);
		};
		forEach(consumer);
		return DelegateInitResult.SUCCESS;
	}
	
	@Override
	public Function getFunction(String namespace, Short contextId) {
		
		@SuppressWarnings("unchecked")
		Map<Short, Function> functionMap = (Map<Short, Function>) get(namespace);

		if (functionMap != null) {
			return functionMap.get(contextId);
		}

		return null;
	}

	@Override
	public Function getFunction(Short id) {
		return functions.get(id);
	}

	@Override
	public Short getId(Function function) {
		return functionIds.get(toString(function));
	}

	private static String toString(Function function) {
		return function.namespace() + "_" + function.contextId();
	}

	@Override
	protected void add(List<Class<Function>> classes) {

		if (classes.size() + functionIds.size() > Short.MAX_VALUE) {
			Exceptions.throwRuntime(PlatformException.get(ClusterFunctionError.MAX_NUMBER_OF_FUNCTIONS_REACHED));
		}

		classes.forEach(c -> {
			addFunction(c);
		});
	}

	@Override
	protected void remove(List<Class<Function>> classes) {
		classes.forEach(c -> {
			removeFunction(c);
		});
	}

	private void addFunction(Class<Function> c) {

		Function[] functions = c.getEnumConstants();

		if (functions.length == 0) {
			return;
		}

		String namespace = functions[0].namespace();

		Map<Short, Function> functionMap = new HashMap<>(functions.length);

		for (Function f : functions) {
			functionMap.put(f.contextId(), f);
		}

		@SuppressWarnings("unchecked")
		Map<Short, Function> existingMap = (Map<Short, Function>) get(namespace);

		if (existingMap == null) {
			set(namespace, functionMap);
		} else {
			
			functionMap.forEach((k, v) -> {
				if(existingMap.containsKey(k)) {
					throw new RuntimeException("Namespace: " + namespace + " already contains context-id: " + k + " => " + ClassUtils.toString(c));
				}
				existingMap.put(k, v);
			});
		}
		
		for (Function f : functions) {
			
			Short id = (short) functionIds.size();
			
			functionIds.put(toString(f), id);
			FunctionDelegate.functions.put(id, f);
		}
	}

	private void removeFunction(Class<Function> c) {

		Function[] functions = c.getEnumConstants();
		
		if (functions.length > 0) {
			return;
		}

		String namespace = functions[0].namespace();

		@SuppressWarnings("unchecked")
		Map<Short, Function> existingMap = (Map<Short, Function>) get(namespace);

		for (Function f : functions) {
			existingMap.remove(f.contextId());
		}

		if (existingMap.isEmpty()) {
			remove(namespace);
		}
		
		for (Function f : functions) {
			Short id = functionIds.remove(toString(f));
			FunctionDelegate.functions.remove(id);
		}
	}
	
}
