package com.re.paas.internal.infra.database.dynamodb.classes;

public enum ReturnValue {

	NONE("NONE"), ALL_OLD("ALL_OLD"), UPDATED_OLD("UPDATED_OLD"), ALL_NEW("ALL_NEW"), UPDATED_NEW("UPDATED_NEW");

	private String value;

	private ReturnValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	/**
	 * Use this in place of valueOf.
	 *
	 * @param value real value
	 * @return ReturnValue corresponding to the value
	 *
	 * @throws IllegalArgumentException If the specified value does not map to one
	 *                                  of the known values in this enum.
	 */
	public static ReturnValue fromValue(String value) {
		if (value == null || "".equals(value)) {
			throw new IllegalArgumentException("Value cannot be null or empty!");
		}

		for (ReturnValue enumEntry : ReturnValue.values()) {
			if (enumEntry.toString().equals(value)) {
				return enumEntry;
			}
		}

		throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
	}
}