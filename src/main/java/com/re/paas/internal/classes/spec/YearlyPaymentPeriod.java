package com.re.paas.internal.classes.spec;

/**
 * This enum assumes that the default period is MONTHLY. And this is reflected
 * in the multiplier
 */
public enum YearlyPaymentPeriod {

	DAILY(0, MultiplierType.MULTIPLY, 365), WEEKLY(1, MultiplierType.MULTIPLY, 52), MONTHLY(2, MultiplierType.MULTIPLY,
			12), YEARLY(3, MultiplierType.MULTIPLY, 1), TWO_YEARLY(4, MultiplierType.DIVIDE, 2);

	private final MultiplierType multiplierType;
	private final Integer ratio;

	private final int value;

	private YearlyPaymentPeriod(Integer value, MultiplierType multiplierType, Integer ratio) {
		this.multiplierType = multiplierType;
		this.ratio = ratio;
		this.value = value;
	}

	public static YearlyPaymentPeriod from(int value) {

		switch (value) {

		case 0:
			return YearlyPaymentPeriod.DAILY;
		case 1:
			return YearlyPaymentPeriod.WEEKLY;
		case 2:
			return YearlyPaymentPeriod.MONTHLY;
		case 3:
			return YearlyPaymentPeriod.YEARLY;
		case 4:
			return YearlyPaymentPeriod.TWO_YEARLY;
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public MultiplierType getMultiplierType() {
		return multiplierType;
	}

	public Integer getRatio() {
		return ratio;
	}

	public int getValue() {
		return value;
	}

}
