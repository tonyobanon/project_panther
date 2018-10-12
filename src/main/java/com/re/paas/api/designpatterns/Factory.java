package com.re.paas.api.designpatterns;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Factory {

	private static final Map<String, Function<Object[], Object>> constructors = new HashMap<>();

	
	public static <T> T get(Class<T> type, Object... parameters) {
		@SuppressWarnings("unchecked")
		T o = (T) constructors.get(type.getName()).apply(parameters);
		return o;
	}
	
	public static <T> void register (Class<T> type, Function<Object[], T> constructor) {
		
		assert !constructors.containsKey(type.getName());
		
		@SuppressWarnings("unchecked")
		Function<Object[], Object> c = (Function<Object[], Object>) constructor;
		constructors.put(type.getName(), c);
	}
	
}
