package com.re.paas.api.designpatterns;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.threadsecurity.ThreadSecurity;

public class Singleton {

	private static final Map<String, Object> singletons = new HashMap<>();

	public static <T> T get(Class<T> type) {
		
		@SuppressWarnings("unchecked")
		T o = (T) singletons.get(type.getName());
		return o;
	}

	public static <T> void register(Class<T> type, T typeSubType) {
		
		if(!ThreadSecurity.get().isTrusted()) {
			assert !singletons.containsKey(type.getName());
		}
		
		singletons.put(type.getName(), typeSubType);
	}

}
