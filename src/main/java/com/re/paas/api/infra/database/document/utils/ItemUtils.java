package com.re.paas.api.infra.database.document.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.database.document.IncompatibleTypeException;
import com.re.paas.api.infra.database.model.KeySchemaElement;
import com.re.paas.api.infra.database.model.KeyType;
import com.re.paas.api.utils.ValidationUtils;

public class ItemUtils {

	public static void checkInvalidAttrName(String attrName) {
	    if (attrName == null || attrName.trim().length() == 0)
	        throw new IllegalArgumentException("Attribute name must not be null or empty");
	}

	public static void checkInvalidAttribute(String attrName, Object val) {
	    checkInvalidAttrName(attrName);
	    ValidationUtils.assertNotNull(val, "val");
	}

	public static Set<BigDecimal> toBigDecimalSet(Set<Number> vals) {
	    Set<BigDecimal> set = new LinkedHashSet<BigDecimal>(vals.size());
	    for (Number n: vals)
	        set.add(ItemUtils.toBigDecimal(n));
	    return set;
	}

	public static Set<BigDecimal> toBigDecimalSet(Number ... val) {
	    Set<BigDecimal> set = new LinkedHashSet<BigDecimal>(val.length);
	    for (Number n: val)
	        set.add(ItemUtils.toBigDecimal(n));
	    return set;
	}

	/**
	 * Converts a number into BigDecimal representation.
	 */
	public static BigDecimal toBigDecimal(Number n) {
	    if (n instanceof BigDecimal)
	        return (BigDecimal)n;
	    return new BigDecimal(n.toString());
	}
	   
	/**
     * Returns the string representation of the given value; or null if the
     * value is null. For <code>BigDecimal</code> it will be the string
     * representation without an exponent field.
     */
    public static String valToString(Object val) {
        if (val instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal)val;
            return bd.toPlainString();
        }
        if (val == null)
            return null;
        if (val instanceof String
            ||  val instanceof Boolean
            ||  val instanceof Number)
            return val.toString();
        throw new IncompatibleTypeException("Cannot convert " + val.getClass() + " into a string");
    }
    
	public static boolean isNumber(Class<?> clazz, String fieldName) {
		try {

			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			Class<?> fieldType = field.getType();

			if (fieldType.getSuperclass().getTypeName().equals("java.lang.Number")) {
				return true;
			}

		} catch (NoSuchFieldException e) {
			Exceptions.throwRuntime(e);
		}

		return false;
	}

	public static String getScalarType(Class<?> clazz, String fieldName) {

		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Exceptions.throwRuntime(e);
		}

		field.setAccessible(true);
		Class<?> fieldType = field.getType();

		if (fieldType.getTypeName().equals("java.nio.ByteBuffer")) {
			return "B";
		}

		try {
			if (fieldType.getSuperclass().getTypeName().equals("java.lang.Number")) {
				return "N";
			}
		} catch (NullPointerException e) {
		}

		if (fieldType.getTypeName().equals("java.lang.String")) {
			return "S";
		}

		throw new RuntimeException(
				"Could not determine scalar type for " + clazz.getSimpleName() + "/" + field.getName());
	}

	public static boolean isScalarType(Class<?> clazz, String fieldName) {

		try {

			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			Class<?> fieldType = field.getType();

			if (fieldType.getTypeName().equals("java.nio.ByteBuffer")) {
				return true;
			} else if (fieldType.getSuperclass().getTypeName().equals("java.lang.Number")) {
				return true;
			} else if (fieldType.getTypeName().equals("java.lang.String")) {
				return true;
			}

		} catch (NoSuchFieldException e) {
			Exceptions.throwRuntime(e);
		}

		return false;
	}

	public static String getSchemaKey(Collection<KeySchemaElement> keys, KeyType type) {
		for (KeySchemaElement e : keys) {
			if (e.getKeyType() == type) {
				return e.getAttributeName();
			}
		}
		return null;
	}
    
}
