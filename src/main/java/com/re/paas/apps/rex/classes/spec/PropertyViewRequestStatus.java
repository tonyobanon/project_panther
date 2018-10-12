package com.re.paas.apps.rex.classes.spec;

public enum PropertyViewRequestStatus {

	PENDING(0), ACCEPTED(1), DECLINED(2), COMPLETED(3);

	private int value;

	private PropertyViewRequestStatus(Integer value) {
		this.value = value;
	}

	public static PropertyViewRequestStatus from(int value) {

		switch (value) {

		case 0:
			return PropertyViewRequestStatus.PENDING;

		case 1:
			return PropertyViewRequestStatus.ACCEPTED;

		case 2:
			return PropertyViewRequestStatus.DECLINED;
			
		case 3:
			return PropertyViewRequestStatus.COMPLETED;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
