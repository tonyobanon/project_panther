package com.re.paas.internal.caching;

import java.util.List;
import java.util.Map;
import com.re.paas.api.utils.Utils;

//Used for caching User functionalities, Listing Context
public class CacheAdapter {

	public static void start() {		

	}

	/**
	 * Unlike put(..), this method returns the new key mapped to the specified value
	 * */
	public static Object putTemp(Object value) {
		String key = Utils.newRandom();
		put(CacheType.VOLATILE, key, value);
		return key;
	}

	public static Object put(String key, Object value) {
		return put(null, key, value);
	}

	public static Object put(CacheType type, String key, Object value) {
		return null;
	}

	public static Object del(String key) {
		return del(null, key);
	}

	public static Object del(CacheType type, String key) {
		return null;
	}

	public static Object get(CacheType type, String key) {
		return null;
	}

	public static Object get(String key) {
		return get(null, key);
	}
	
	public static Boolean containsKey(CacheType type, String key) {
		return null;
	}
	
	public static List<String> getList(CacheType type, String key) {
		return null;
	}
	
	public static Map<String, Object> getMap(CacheType type, String key) {
		return null;
	}

	public static Integer getInt(String key) {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		return Integer.parseInt(key);
	}
}
