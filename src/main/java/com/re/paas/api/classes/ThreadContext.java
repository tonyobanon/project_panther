package com.re.paas.api.classes;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.annotations.develop.Todo;

@Todo("Due to time-sensitive scenarios in which ThreadContext is used, probe performance")
public class ThreadContext {

	
	private static final String IS_WEB_REQUEST = "is_web_request";
	private static final String APP_ID = "app_id";

	private static final ThreadContext instance = new ThreadContext();

	private static ThreadLocal<Map<String, Object>> threadAttrs = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};

	public static Boolean empty() {
		return threadAttrs.get().isEmpty();
	}

	public static Object get(String key) {
		return threadAttrs.get().get(key);
	}

	public static ThreadContext set(String key, Object value) {
		threadAttrs.get().put(key, value);
		return instance;
	}

	public static ThreadContext remove(String key) {
		threadAttrs.get().remove(key);
		return instance;
	}

	public static ThreadContext clear() {
		threadAttrs.get().clear();
		return instance;
	}

	/**
	 * Initialize a request context for the current thread
	 */
	public static ThreadContext newRequestContext(String appId, Boolean isWebRequest) {
		Map<String, Object> o = threadAttrs.get();
		assert o.isEmpty();
		
		o.put(IS_WEB_REQUEST, isWebRequest);
		o.put(APP_ID, appId);
		
		return instance;
	}

	/**
	 * This checks if the current thread executes within the context of a HTTP
	 * request
	 */
	public static boolean isWebRequest() {
		Boolean o = (Boolean) threadAttrs.get().get(IS_WEB_REQUEST);
		return o != null && o == true;
	}
	
	public static String getAppId() {
		return (String) threadAttrs.get().get(APP_ID);
	}

}
