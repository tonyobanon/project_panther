package com.re.paas.api.utils;

import java.util.Map;

import com.re.paas.api.designpatterns.Singleton;

public interface JsonParser {

	public static JsonParser get() {
		return Singleton.get(JsonParser.class);
	}
	
	String toPrettyJson(Object value);
	
	<T> String toJson(T o);

	<T> T fromJson(String json, Class<T> type);
	
	<T> T fromMap(Map<String, Object> map, Class<T> type);

	<T, V> Map<String, Object> toMap(T obj);
}
