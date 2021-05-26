package com.re.paas.internal.errors.impl;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.errors.AbstractErrorSpiDelegate;
import com.re.paas.api.errors.Error;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourceStatus;

public class ErrorSpiDelegate extends AbstractErrorSpiDelegate {

	@Override
	public DelegateInitResult init() {
		
		addResources(this::add);
		
		return DelegateInitResult.SUCCESS;
		
	}

	protected ResourceStatus add(Class<Error> c) {

		Error[] errors = c.getEnumConstants();

		if (errors.length == 0) {
			return ResourceStatus.NOT_UPDATED;
		}

		String namespace = errors[0].namespace();

		Map<Integer, String> errorMap = new HashMap<Integer, String>(errors.length);

		for (Error e : errors) {
			errorMap.put(e.getCode(), e.getMessage());
		}

		@SuppressWarnings("unchecked")
		Map<Integer, String> existingMap = (Map<Integer, String>) getLocalStore().get(namespace);

		if (existingMap == null) {
			getLocalStore().put(namespace, errorMap);
		} else {
			
			for(Map.Entry<Integer,String> e  : errorMap.entrySet()) {
				
				if (existingMap.containsKey(e.getKey())) {
					return ResourceStatus.ERROR.setMessage("Namespace: " + namespace + " already contains code: " + e.getKey() + " => " + c.getName());
				}
				
				existingMap.put(e.getKey(), e.getValue());
			}
		}
		
		return ResourceStatus.UPDATED;
	}

	protected ResourceStatus remove(Class<Error> c) {

		Error[] errors = c.getEnumConstants();

		if (errors.length > 0) {
			return ResourceStatus.NOT_UPDATED;
		}

		String namespace = errors[0].namespace();

		@SuppressWarnings("unchecked")
		Map<Integer, String> existingMap = (Map<Integer, String>) getLocalStore().get(namespace);

		for (Error e : errors) {
			existingMap.remove(e.getCode());
		}

		if (existingMap.isEmpty()) {
			getLocalStore().remove(namespace);
		}
		
		return ResourceStatus.UPDATED;
	}

	@Override
	public String getError(String namespace, Integer code) {

		@SuppressWarnings("unchecked")
		Map<Integer, String> errorMap = (Map<Integer, String>) getLocalStore().get(namespace);

		if (errorMap != null) {
			return errorMap.get(code);
		}

		return null;
	}

}
