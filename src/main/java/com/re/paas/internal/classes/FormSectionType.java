package com.re.paas.internal.classes;

public enum FormSectionType {

	APPLICATION_FORM(0), SYSTEM_CONFIGURATION(1);

	private int value;

	private FormSectionType(int value) {
		this.value = value;
	}

	public static FormSectionType from(int value) {

		switch (value) {

		case 0:
			return APPLICATION_FORM;

		case 1:
			return SYSTEM_CONFIGURATION;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}

}
