package com.re.paas.api.infra.database.document.utils;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import com.re.paas.api.infra.database.document.IncompatibleTypeException;
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
}
