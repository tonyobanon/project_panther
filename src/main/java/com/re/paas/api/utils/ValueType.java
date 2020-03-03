package com.re.paas.api.utils;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum ValueType {
	BINARY(true), BOOLEAN(true), BINARY_SET, DATE(true), LIST, MAP, NUMBER(true), NUMBER_SET, NULL, STRING(true),
	STRING_SET;

	private final Boolean scalar;

	private ValueType() {
		this(false);
	}

	private ValueType(Boolean scalar) {
		this.scalar = scalar;
	}

	public Boolean isScalar() {
		return this.scalar;
	}
	
	public static ValueType getType(Object val) {
        if (val == null)
            return ValueType.NULL;
        if (val instanceof String)
            return ValueType.STRING;
        if (val instanceof Number)
            return ValueType.NUMBER;
        if (val instanceof ByteBuffer)
            return BINARY;
        if (val instanceof Boolean)
            return ValueType.BOOLEAN;
        if (val instanceof Date)
            return DATE;
        if (val instanceof List)
            return LIST;
        if (val instanceof Map) {
            return MAP;
        }
        if (val instanceof Set) {
            Set<?> set = (Set<?>)val;
            // Treat an empty set as a set of String
            if (set.size() == 0) {
                return STRING_SET;
            }
            // Try to locate the first non-null element and use that as the
            // representative type
            Object representative = null;
            for (Object o: set) {
                if (o != null)
                    representative = o;
            }
            // If all elements are null, treat the element type as String
            if (representative == null || representative instanceof String) {
                return STRING_SET;
            }
            if (representative instanceof Number) {
                return NUMBER_SET;
            }
            if (representative instanceof ByteBuffer) {
                return BINARY_SET;
            }
            throw new UnsupportedOperationException("Set of "
                    + representative.getClass() + " is not currently supported");
        }
        throw new UnsupportedOperationException("Input type "
                + val.getClass() + " is not currently supported");
	}

}
