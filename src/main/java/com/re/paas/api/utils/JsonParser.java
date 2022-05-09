package com.re.paas.api.utils;

import java.nio.ByteBuffer;
import java.util.Map;

import com.re.paas.api.Singleton;

public interface JsonParser {

	public static JsonParser get() {
		return Singleton.get(JsonParser.class);
	}
	
	String toPrettyString(Object value);
	
	<T> String toString(T o);
	
	<T> ByteBuffer toBuffer(T o);

	<T> T fromString(String json, Class<T> type);
	
	<T> T fromMap(Map<String, Object> map, Class<T> type);

	<T, V> Map<String, Object> toMap(T obj);
}
