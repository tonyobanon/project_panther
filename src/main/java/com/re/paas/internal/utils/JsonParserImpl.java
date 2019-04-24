package com.re.paas.internal.utils;

import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.classes.Json;

public class JsonParserImpl implements JsonParser {

	@Override
	public String toJsonString(Object value) {
		return Json.toJson(value);
	}

	@Override
	public <T> T fromJsonString(String json, Class<T> clazz) {
		return Json.fromJson(json, clazz);
	}

	@Override
	public String toJsonPrettyString(Object value) {
		return toJsonString(value);
	}

}
