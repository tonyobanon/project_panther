package com.re.paas.api.fusion.components;

public class FieldGetHandle<T> extends Handle {
	
	private final String key;
	
	public FieldGetHandle(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
