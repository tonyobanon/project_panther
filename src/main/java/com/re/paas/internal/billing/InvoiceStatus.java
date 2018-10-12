package com.re.paas.internal.billing;

public enum InvoiceStatus {

	CREATED(0, false, false), PENDING(1, true, true), DEFAULTING(2, true, true), COMPLETED(3, false, false);

	private boolean isUpdatable;
	private boolean isOutstanding;
	private int value;

	private InvoiceStatus(Integer value, boolean isOutstanding, boolean isUpdatable) {
		this.value = value;
		this.isOutstanding = isOutstanding;
		this.isUpdatable = isUpdatable;
	}

	public static InvoiceStatus from(int value) {

		switch (value) {

		case 3:
			return InvoiceStatus.COMPLETED;
			
		case 2:
			return InvoiceStatus.DEFAULTING;
			
		case 1:
			return InvoiceStatus.PENDING;
			
		case 0:
			return InvoiceStatus.CREATED;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public Integer getValue() {
		return value;
	}

	public boolean isOutstanding() {
		return isOutstanding;
	}

	public boolean isUpdatable() {
		return isUpdatable;
	}
}
