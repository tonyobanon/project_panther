package com.re.paas.internal.serialization;

import java.util.HashSet;
import java.util.Set;

import com.re.paas.api.classes.Exceptions;

public class Primitives {

	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	private static final String PREFIX = "^*";
	private static final Integer PREFIX_LENGTH = 4;

	private static final String BOOLEAN = PREFIX + "BO";
	private static final String CHAR = PREFIX + "CH";
	private static final String BYTE = PREFIX + "BY";
	private static final String SHORT = PREFIX + "SH";
	private static final String INTEGER = PREFIX + "IN";
	private static final String LONG = PREFIX + "LN";
	private static final String FLOAT = PREFIX + "FL";
	private static final String DOUBLE = PREFIX + "DB";

	public static Boolean isWrapperType(Object o) {
		return isWrapperType(o.getClass());
	}
	
	public static Boolean isWrapperType(Class<?> clazz) {
		return WRAPPER_TYPES.contains(clazz) || clazz.equals(String.class);
	}

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

	/**
	 * This returns a string representation for the specified object. The object
	 * passed in must be a primitive (an instance of a primitive class wrapper)
	 * 
	 * @param o
	 * @return
	 */
	public static String toString(Object o) {

		StringBuilder r = new StringBuilder();
		Class<?> clazz = o.getClass();

		if (clazz == Boolean.class) {
			r.append(BOOLEAN).append(((Boolean) o) == true ? "1" : "0");

		} else if (clazz == Character.class) {
			r.append(CHAR).append((Character) o);

		} else if (clazz == Byte.class) {
			r.append(BYTE).append(Byte.toString((byte) o));

		} else if (clazz == Short.class) {
			r.append(SHORT).append((Short) o);

		} else if (clazz == Integer.class) {
			r.append(INTEGER).append((Integer) o);

		} else if (clazz == Long.class) {
			r.append(LONG).append((Long) o);

		} else if (clazz == Float.class) {
			r.append(FLOAT).append((Float) o);

		} else if (clazz == Double.class) {
			r.append(DOUBLE).append((Double) o);
		}

		if (r.length() == 0) {
			Exceptions.throwRuntime(new IllegalArgumentException("The parameter must be a primitive type"));
		}

		return r.toString();
	}

	public static Object fromString(String o) {

		if ((!o.startsWith(PREFIX)) || o.length() <= PREFIX_LENGTH) {
			return null;
		}

		String type = o.substring(0, PREFIX_LENGTH);

		String data = o.substring(PREFIX_LENGTH, o.length());

		if (data.isEmpty()) {
			return null;
		}
		
		Object r = null;

		switch (type) {

		case BOOLEAN:
			r = data.equals("1") ? true : false;
			break;

		case CHAR:
			r = data.charAt(0);
			break;

		case BYTE:
			r = Byte.parseByte(data);
			break;

		case SHORT:
			r = Short.parseShort(data);
			break;

		case INTEGER:
			r = Integer.parseInt(data);
			break;

		case LONG:
			r = Long.parseLong(data);
			break;

		case FLOAT:
			r = Float.parseFloat(data);
			break;

		case DOUBLE:
			r = Double.parseDouble(data);
			break;
		}

		return r;
	}

}
