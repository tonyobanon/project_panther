package com.re.paas.api.utils;

import java.util.HashSet;
import java.util.Set;

public class Primitives {
	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	private static Set<Class<?>> getWrapperTypes() {
		Set<Class<?>> ret = new HashSet<Class<?>>();
		ret.add(Boolean.class);
		ret.add(Character.class);
		ret.add(Byte.class);
		ret.add(Short.class);
		ret.add(Integer.class);
		ret.add(Long.class);
		ret.add(Float.class);
		ret.add(Double.class);
		return ret;
	}

	public static Boolean isWrapperType(Class<?> clazz) {
		return WRAPPER_TYPES.contains(clazz);
	}
}
