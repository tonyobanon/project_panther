package com.re.paas.apps.rex.classes.spec;

public enum PropertyContractType {

	RENT(0), SALE(1);

	private int value;

	private PropertyContractType(Integer value) {
		this.value = value;
	}

	public static PropertyContractType from(int value) {

		switch (value) {

		case 0:
			return PropertyContractType.RENT;

		case 1:
			return PropertyContractType.SALE;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
