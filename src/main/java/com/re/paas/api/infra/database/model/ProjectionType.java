package com.re.paas.api.infra.database.model;

public enum ProjectionType {
	ALL("ALL"), KEYS_ONLY("KEYS_ONLY"), INCLUDE("INCLUDE");

	private String value;

	private ProjectionType(String value) {
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
	 * @return ProjectionType corresponding to the value
	 *
	 * @throws IllegalArgumentException If the specified value does not map to one
	 *                                  of the known values in this enum.
	 */
	public static ProjectionType fromValue(String value) {
		if (value == null || "".equals(value)) {
			throw new IllegalArgumentException("Value cannot be null or empty!");
		}

		for (ProjectionType enumEntry : ProjectionType.values()) {
			if (enumEntry.toString().equals(value)) {
				return enumEntry;
			}
		}

		throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
	}
}
