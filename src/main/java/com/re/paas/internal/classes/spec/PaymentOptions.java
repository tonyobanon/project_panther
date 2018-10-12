package com.re.paas.internal.classes.spec;

public enum PaymentOptions {

	ONE_TIME(0), INSTALLMENT_MONTHLY(1), INSTALLMENT_QUARTERLY(2);

	private int value;

	private PaymentOptions(Integer value) {
		this.value = value;
	}

	public static PaymentOptions from(int value) {

		switch (value) {

		case 0:
			return PaymentOptions.ONE_TIME;

		case 1:
			return PaymentOptions.INSTALLMENT_MONTHLY;

		case 2:
			return PaymentOptions.INSTALLMENT_QUARTERLY;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
