package com.re.paas.integrated.models.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.caching.CacheType;

public class CacheHelper {

	
	//=======================	SIMPLE COLLECTION UTILITIES		=========================//
	
	
	//	LISTS

	
	public static List<String>  getList(CacheType cacheType, String key){
		return getListOrDefault(cacheType, key, null);
	}
	
	public static List<String>  getListOrDefault(CacheType cacheType, String key, Callable<List<String>> fetch) {
		List<String> value = CacheAdapter.getList(cacheType, key);
		if(value == null && fetch != null) {
			try {
				value = fetch.call();
			} catch (Exception e) {
				Exceptions.throwRuntime(e);
			}
			CacheAdapter.put(cacheType, key, value);
		}
		return value;
	}
	
	public static void addToList(CacheType cacheType, String key, String element) {
		List<String> value = CacheAdapter.getList(cacheType, key);
		if(value != null) {
			value.add(element);
			CacheAdapter.put(cacheType, key, value);
		}
	}
	
	public static void addToListOrCreate(CacheType cacheType, String key, String element) {
		List<String> value = CacheAdapter.getList(cacheType, key);
		if(value == null) {
			value = new ArrayList<String>();
		}
		value.add(element);
		CacheAdapter.put(cacheType, key, value);
	}
	
	public static void addToListOrCreate(CacheType cacheType, String key, List<String> elements) {
		List<String> value = CacheAdapter.getList(cacheType, key);
		if(value == null) {
			value = new ArrayList<String>();
		}
		value.addAll(elements);
		CacheAdapter.put(cacheType, key, value);
	}
	
	public static void  removeFromList(CacheType cacheType, String key, String elem) {
		List<String> value = CacheAdapter.getList(cacheType, key);
		if(value != null) {
			value.remove(elem);
			CacheAdapter.put(cacheType, key, value);
		}
	}
	
	
	
	
	//	MAP

	public static Object getMapEntry(CacheType cacheType, String key, String k) {
		Map<String, Object> value = CacheAdapter.getMap(cacheType, key);
		return value != null ? value.get(k) : null;
	}
	
	public static Map<String, Object>  getMap(CacheType cacheType, String key){
		return getMapOrDefault(cacheType, key, null);
	}
	
	public static Map<String, Object>  getMapOrDefault(CacheType cacheType, String key, Callable<Map<String, Object>> fetch) {
		Map<String, Object> value = CacheAdapter.getMap(cacheType, key);
		if(value == null && fetch != null) {
				try {
					value = fetch.call();
				} catch (Exception e) {
					Exceptions.throwRuntime(e);
				}
				
				CacheAdapter.put(cacheType, key, value);
		}
		return value;
	}
	
	public static void addToMap(CacheType cacheType, String key, String k, Object v) {
		Map<String, Object> value = CacheAdapter.getMap(cacheType, key);
		if(value != null) {
			value.put(k, v);
			CacheAdapter.put(cacheType, key, value);
		}
	}
	
	public static void addToMap(CacheType cacheType, String key, Map<String, Object> elements) {
		Map<String, Object> value = CacheAdapter.getMap(cacheType, key);
		if(value != null) {
			value.putAll(elements);
			CacheAdapter.put(cacheType, key, value);
		}
	}
	
	public static void addToMapOrCreate(CacheType cacheType, String key, String k, Object v) {
		Map<String, Object> value = CacheAdapter.getMap(cacheType, key);
		if(value == null) {
			value = new HashMap<String, Object>();
		}
		value.put(k, v);
		CacheAdapter.put(cacheType, key, value);
	}
	
	public static void addToMapOrCreate(CacheType cacheType, String key, Map<String, Object> elements) {
		Map<String, Object> value = CacheAdapter.getMap(cacheType, key);
		if(value == null) {
			value = new HashMap<String, Object>();
		}
		value.putAll(elements);
		CacheAdapter.put(cacheType, key, value);
	}
	
	public static void  removeFromMap(CacheType cacheType, String key, String k) {
		Map<String, Object> value = CacheAdapter.getMap(cacheType, key);
		if(value != null) {
			value.remove(k);
			CacheAdapter.put(cacheType, key, value);
		}
	}
	

	//=======================	SIMPLE COLLECTION UTILITIES		=========================//

}
