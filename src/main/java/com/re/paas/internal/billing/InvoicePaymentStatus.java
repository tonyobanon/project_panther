package com.re.paas.internal.billing;

public enum InvoicePaymentStatus {

	CREATED(0), PENDING_AUTHORIZATION(1), AUTHORIZATION_FAILED(2), AUTHORIZATION_SUCCESS(3), PENDING_CAPTURE(4), CAPTURE_COMPLETE(5), CAPTURE_FAILED(6), CANCELLED(7), REFUNDED(
			8), PENDING_3D_SECURE_AUTHORIZATION(9);

	private int value;

	private InvoicePaymentStatus(Integer value) {
		this.value = value;
	}

	public static InvoicePaymentStatus from(int value) {

		switch (value) {
		case 9:
			return InvoicePaymentStatus.PENDING_3D_SECURE_AUTHORIZATION;
		case 8:
			return InvoicePaymentStatus.REFUNDED;
		case 7:
			return InvoicePaymentStatus.CANCELLED;
		case 6:
			return InvoicePaymentStatus.CAPTURE_FAILED;
		case 5:
			return InvoicePaymentStatus.CAPTURE_COMPLETE;
		case 4:
			return InvoicePaymentStatus.PENDING_CAPTURE;
		case 3:
			return InvoicePaymentStatus.AUTHORIZATION_SUCCESS;
		case 2:
			return InvoicePaymentStatus.AUTHORIZATION_FAILED;
		case 1:
			return InvoicePaymentStatus.PENDING_AUTHORIZATION;
		case 0:
			return InvoicePaymentStatus.CREATED;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public Integer getValue() {
		return value;
	}
}
