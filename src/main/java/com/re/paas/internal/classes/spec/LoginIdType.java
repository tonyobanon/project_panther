package com.re.paas.internal.classes.spec;

public enum LoginIdType {

	EMAIL(0), PHONE(1);

	private int value;

	private LoginIdType(Integer value) {
		this.value = value;
	}

	public static LoginIdType from(int value) {

		switch (value) {

		case 0:
			return LoginIdType.EMAIL;

		case 1:
			return LoginIdType.PHONE;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
