package com.re.paas.internal.utils;

import java.util.Map;

import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.classes.Json;

public class JsonParserImpl implements JsonParser {

	@Override
	public String toPrettyJson(Object value) {
		return toJson(value);
	}

	@Override
	public <T> String toJson(T o) {
		return Json.toJson(o);
	}

	@Override
	public <T, V> Map<String, Object> toMap(T obj) {
		return Json.toMap(obj);
	}
	
	@Override
	public <T> T fromJson(String json, Class<T> type) {
		return Json.fromJson(json, type);
	}

	@Override
	public <T> T fromMap(Map<String, Object> map, Class<T> type) {
		return Json.fromMap(map, type);
	}

}
