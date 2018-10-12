package com.re.paas.api.classes;

public class ObjectWrapper<T> {

	protected T value;
	
	public ObjectWrapper() {
	}
	
	public ObjectWrapper(T value) {
		super();
		this.value = value;
	}

	public ObjectWrapper<T> set(T value) {
		this.value = value;
		return this;
	}
	
	public T get() {
		return value;
	} 
	
	@Override
	public String toString() {
		return value.toString();
	}
	
}
