package com.re.paas.internal.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.classes.Json;

public class JsonParserImpl implements JsonParser {

	@Override
	public String toPrettyString(Object value) {
		return toString(value);
	}

	@Override
	public <T> String toString(T o) {
		return Json.toJson(o);
	}
	
	@Override
	public <T> ByteBuffer toBuffer(T o) {
	
		StringBuilder sb = new StringBuilder();
		
		Json.getGson().toJson(o, new Appendable() {
			
			@Override
			public Appendable append(CharSequence csq, int start, int end) throws IOException {
				return this.append(csq.subSequence(start, end));
			}
			
			@Override
			public Appendable append(char c) throws IOException {
				sb.append(Character.toString(c));
				return this;
			}
			
			@Override
			public Appendable append(CharSequence csq) throws IOException {
				sb.append(csq.toString());
				return this;
			}
		});
		
		return ByteBuffer.wrap(sb.toString().getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public <T, V> Map<String, Object> toMap(T obj) {
		return Json.toMap(obj);
	}
	
	@Override
	public <T> T fromString(String json, Class<T> type) {
		return Json.fromJson(json, type);
	}

	@Override
	public <T> T fromMap(Map<String, Object> map, Class<T> type) {
		return Json.fromMap(map, type);
	}

}
