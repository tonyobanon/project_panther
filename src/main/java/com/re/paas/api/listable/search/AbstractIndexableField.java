package com.re.paas.api.listable.search;

import java.nio.ByteBuffer;

public interface AbstractIndexableField {

	/**
	 * {@link IndexableFieldType} describing the properties of this field.
	 */
	IndexableFieldType fieldType();

	/** Field name */
	String name();
	
	Object value();

	/** Non-null if this field has a binary value */
	ByteBuffer binaryValue();

	/** Non-null if this field has a string value */
	String stringValue();

	/**
	 * Non-null if this field has a string value
	 */
	default CharSequence getCharSequenceValue() {
		return stringValue();
	}

	/** Non-null if this field has a numeric value */
	Number numericValue();
}
