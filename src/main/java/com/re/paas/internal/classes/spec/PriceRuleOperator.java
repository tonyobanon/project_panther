package com.re.paas.internal.classes.spec;

public enum PriceRuleOperator {

	PLUS(0), MINUS(1);

	private int value;

	private PriceRuleOperator(Integer value) {
		this.value = value;
	}

	public static PriceRuleOperator from(int value) {

		switch (value) {

		case 0:
			return PriceRuleOperator.PLUS;

		case 1:
			return PriceRuleOperator.MINUS;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
