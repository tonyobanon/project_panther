package com.re.paas.internal.billing;

public enum PaymentStatus {

	CREATED(1), AUTHORIZATION_FAILED(2), AUTHORIZATION_SUCCESS(3), PENDING_CAPTURE(4), CAPTURE_COMPLETE(5),
	CAPTURE_FAILED(6), CANCELLED(7), REFUNDED(8), PENDING_3D_SECURE_AUTHORIZATION(9);

	private int value;

	private PaymentStatus(Integer value) {
		this.value = value;
	}

	public static PaymentStatus from(int value) {

		switch (value) {
		case 9:
			return PaymentStatus.PENDING_3D_SECURE_AUTHORIZATION;
		case 8:
			return PaymentStatus.REFUNDED;
		case 7:
			return PaymentStatus.CANCELLED;
		case 6:
			return PaymentStatus.CAPTURE_FAILED;
		case 5:
			return PaymentStatus.CAPTURE_COMPLETE;
		case 4:
			return PaymentStatus.PENDING_CAPTURE;
		case 3:
			return PaymentStatus.AUTHORIZATION_SUCCESS;
		case 2:
			return PaymentStatus.AUTHORIZATION_FAILED;
		case 1:
			return PaymentStatus.CREATED;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public Integer getValue() {
		return value;
	}
}
