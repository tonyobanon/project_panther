package com.re.paas.api.utils;

import com.re.paas.api.designpatterns.Singleton;

public interface JsonParser {

	public static JsonParser get() {
		return Singleton.get(JsonParser.class);
	}
	
	String toJsonPrettyString(Object value);
	
	String toJsonString(Object value);
	
	<T> T fromJsonString(String json, Class<T> clazz);
}
