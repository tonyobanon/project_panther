package com.re.paas.internal.errors.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.errors.AbstractErrorSpiDelegate;
import com.re.paas.api.errors.Error;
import com.re.paas.api.runtime.spi.DelegateInitResult;

public class ErrorSpiDelegate extends AbstractErrorSpiDelegate {

	@Override
	public DelegateInitResult init() {

		Consumer<Class<Error>> consumer = c -> {
			addError(c);
		};

		forEach(consumer);
		return DelegateInitResult.SUCCESS;
	}

	private void addError(Class<Error> c) {

		Error[] errors = c.getEnumConstants();

		if (errors.length == 0) {
			return;
		}

		String namespace = errors[0].namespace();

		Map<Integer, String> errorMap = new HashMap<Integer, String>(errors.length);

		for (Error e : errors) {
			errorMap.put(e.getCode(), e.getMessage());
		}

		@SuppressWarnings("unchecked")
		Map<Integer, String> existingMap = (Map<Integer, String>) get(namespace);

		if (existingMap == null) {
			set(namespace, errorMap);
		} else {

			errorMap.forEach((k, v) -> {
				if (existingMap.containsKey(k)) {
					throw new RuntimeException(
							"Namespace: " + namespace + " already contains code: " + k + " => " + c.getName());
				}
				existingMap.put(k, v);
			});
		}
	}

	private void removeError(Class<Error> c) {

		Error[] errors = c.getEnumConstants();

		if (errors.length > 0) {
			return;
		}

		String namespace = errors[0].namespace();

		@SuppressWarnings("unchecked")
		Map<Integer, String> existingMap = (Map<Integer, String>) get(namespace);

		for (Error e : errors) {
			existingMap.remove(e.getCode());
		}

		if (existingMap.isEmpty()) {
			remove(namespace);
		}
	}

	@Override
	public String getError(String namespace, Integer code) {

		@SuppressWarnings("unchecked")
		Map<Integer, String> errorMap = (Map<Integer, String>) get(namespace);

		if (errorMap != null) {
			return errorMap.get(code);
		}

		return null;
	}

	@Override
	protected void add(List<Class<Error>> classes) {
		classes.forEach(c -> {
			addError(c);
		});
	}

	@Override
	protected List<Class<Error>> remove(List<Class<Error>> classes) {
		classes.forEach(c -> {
			removeError(c);
		});
		return Collections.emptyList();
	}
	
}
