package com.re.paas.api.fusion.components;

public class FieldInfo<T>{
 
	private final String key;

	FieldInfo(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
}
