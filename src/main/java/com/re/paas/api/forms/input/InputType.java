package com.re.paas.api.forms.input;

public enum InputType {

	HIDDEN(1), FILE(2), TEXT(3), EMAIL(4), SECRET(5), BOOLEAN(6), PHONE(7), DATE(8), NUMBER(9), NUMBER_2L(10),
	NUMBER_3L(11), NUMBER_4L(12), AMOUNT(13), IMAGE(14), PLAIN(15), SIGNATURE(16), LANGUAGE(17), COUNTRY(18),
	ADDRESS(19), TERRITORY(20), CITY(21), PREFERED_LOCALE(22), TIMEZONE(23);

	private TypeDescriptor descriptor;
	private final Integer value;

	private InputType(Integer value) {
		this.value = value;
	}

	public TypeDescriptor getDescriptor() {
		return descriptor;
	}

	public InputType setDescriptor(TypeDescriptor descriptor) {
		this.descriptor = descriptor;
		return this;
	}

	public Integer getValue() {
		return value;
	}

	public static InputType from(Integer type) {

		switch (type) {

		case 1:
			return HIDDEN;
		case 2:
			return FILE;
		case 3:
			return TEXT;
		case 4:
			return EMAIL;
		case 5:
			return SECRET;
		case 6:
			return BOOLEAN;
		case 7:
			return PHONE;
		case 8:
			return DATE;
		case 9:
			return NUMBER;
		case 10:
			return NUMBER_2L;

		case 11:
			return NUMBER_3L;
		case 12:
			return NUMBER_4L;
		case 13:
			return AMOUNT;
		case 14:
			return IMAGE;
		case 15:
			return PLAIN;
		case 16:
			return SIGNATURE;
		case 17:
			return LANGUAGE;
		case 18:
			return COUNTRY;
		case 19:
			return ADDRESS;
		case 20:
			return TERRITORY;

		case 21:
			return CITY;
		case 22:
			return PREFERED_LOCALE;
		case 23:
			return TIMEZONE;

		default:
			throw new IllegalArgumentException("Invalid type provided");
		}

	}

}
