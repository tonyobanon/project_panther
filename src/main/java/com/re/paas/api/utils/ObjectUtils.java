package com.re.paas.api.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectUtils {

	public static boolean isEmpty(Object obj) {
	
		switch (obj.getClass().getName()) {
		case "java.lang.String":
			return isStringEmpty((String) obj);
		default:
			return false;
		}
	}

	private static boolean isStringEmpty(String s) {
		return s.equals("") || s == null;
	}

	public static byte[] toByteArray(Integer i) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		byte[] bytes = buf.putInt(i).array();
		buf.clear();
		return bytes;
	}

	public static byte[] toByteArray(Short s) {
		ByteBuffer buf = ByteBuffer.allocate(2);
		byte[] bytes = buf.putShort(s).array();
		buf.clear();
		return bytes;
	}

	public static Map<String, String> toStringMap(Map<String, Object> o) {
		Map<String, String> result = new HashMap<>();
		o.forEach((k, v) -> {
			result.put(k, v.toString());
		});
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(T[]... array) {
		List<T> o = new ArrayList<>();

		for(T[] a : array) {
			for(T t : a) {
				o.add(t);
			}
		}		
		return (T[]) o.toArray(new Object[o.size()]);
	}
}
