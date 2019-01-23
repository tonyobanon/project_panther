package com.re.paas.api.spi;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.utils.ClassUtils;

public enum DelegateInitResult {

	SUCCESS, FAILURE, RESOURCE_FAILURE, PENDING_ADAPTER_CONFIGURATION;

	private Object meta;

	public Object getMeta() {
		return meta;
	}
	
	public DelegateInitResult setError(String errorMessage) {
		this.meta = errorMessage;
		return this;
	}
	
	public DelegateInitResult setType(AdapterType type) {
		this.meta = type;
		return this;
	}

	public DelegateInitResult addCulpritResource(Class<?> clazz, String errorMessage) {
		if (this.meta == null) {
			this.meta = new HashMap<>();
		}
		@SuppressWarnings("unchecked")
		Map<String, String> m = ((Map<String, String>) this.meta);
		m.put(ClassUtils.toString(clazz), errorMessage);
		return this;
	}

}
