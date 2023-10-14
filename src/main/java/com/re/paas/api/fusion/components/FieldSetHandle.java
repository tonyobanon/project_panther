package com.re.paas.api.fusion.components;

public class FieldSetHandle<T> extends Handle {
	
	private final String key;
	private T value;
	
	public FieldSetHandle(String key, T value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public T getValue() {
		return value;
	}
	
	void setValue(T value) {
		this.value = value;
	}
}
