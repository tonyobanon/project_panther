package com.re.paas.internal.clustering;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.clustering.AbstractFunctionDelegate;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.errors.ClusterFunctionError;

public class FunctionDelegate extends AbstractFunctionDelegate {

	private static Map<String, Short> functionIds = new HashMap<>();
	private static Map<Short, Function> functions = new HashMap<>();

	@Override
	public DelegateInitResult init() {
		
		this.addResources(this::add);
		
		return DelegateInitResult.SUCCESS;
	}

	@Override
	public Function getFunction(String namespace, Short contextId) {

		@SuppressWarnings("unchecked")
		Map<Short, Function> functionMap = (Map<Short, Function>) getLocalStore().get(namespace);

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

	protected ResourceStatus add(Class<Function> c) {

		if (functionIds.size() + 1 > Short.MAX_VALUE) {
			return ResourceStatus.ERROR.setMessage(
					PlatformException.get(ClusterFunctionError.MAX_NUMBER_OF_FUNCTIONS_REACHED).getMessage());
		}

		Function[] functions = c.getEnumConstants();

		if (functions.length == 0) {
			return ResourceStatus.NOT_UPDATED;
		}

		String namespace = functions[0].namespace();

		Map<Short, Function> functionMap = new HashMap<>(functions.length);

		for (Function f : functions) {
			functionMap.put(f.contextId(), f);
		}

		@SuppressWarnings("unchecked")
		Map<Short, Function> existingMap = (Map<Short, Function>) getLocalStore().get(namespace);

		if (existingMap == null) {
			getLocalStore().put(namespace, functionMap);
		} else {

			for (Map.Entry<Short, Function> e : functionMap.entrySet()) {

				if (existingMap.containsKey(e.getKey())) {
					return ResourceStatus.ERROR.setMessage("Namespace: " + namespace + " already contains context-id: "
							+ e.getKey() + " => " + ClassUtils.toString(c));
				}

				existingMap.put(e.getKey(), e.getValue());
			}
		}

		for (Function f : functions) {

			Short id = (short) functionIds.size();

			functionIds.put(toString(f), id);
			FunctionDelegate.functions.put(id, f);
		}

		return ResourceStatus.UPDATED;
	}

	protected ResourceStatus remove(Class<Function> c) {

		Function[] functions = c.getEnumConstants();

		if (functions.length > 0) {
			return ResourceStatus.NOT_UPDATED;
		}

		String namespace = functions[0].namespace();

		@SuppressWarnings("unchecked")
		Map<Short, Function> existingMap = (Map<Short, Function>) getLocalStore().get(namespace);

		for (Function f : functions) {
			existingMap.remove(f.contextId());
		}

		if (existingMap.isEmpty()) {
			getLocalStore().remove(namespace);
		}

		for (Function f : functions) {
			Short id = functionIds.remove(toString(f));
			FunctionDelegate.functions.remove(id);
		}

		return ResourceStatus.UPDATED;
	}

}
