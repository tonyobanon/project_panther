package com.re.paas.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.re.paas.api.utils.ClassUtils;

public class Factory {

	private static final Map<String, Function<Object[], Object>> constructors = Collections.synchronizedMap(new HashMap<>());

	public static <T> T get(Class<T> type, Object... parameters) {

		String name = ClassUtils.asString(type);

		@SuppressWarnings("unchecked")
		T o = (T) constructors.get(name).apply(parameters);

		if (!ClassUtils.isAccessible(o.getClass())) {
			throw new SecurityException(name + " is not accessible by the current thread");
		}

		return o;
	}

	public static <T> void register(Class<T> type, Function<Object[], T> constructor) {

		String name = ClassUtils.asString(type);

		if (constructors.containsKey(name)) {
			throw new SecurityException(name + " is already registered");
		}

		@SuppressWarnings("unchecked")
		Function<Object[], Object> c = (Function<Object[], Object>) constructor;
		constructors.put(name, c);
	}

}
