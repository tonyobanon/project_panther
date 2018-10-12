package com.re.paas.apps.rex.classes.spec;

public enum PropertyAvailabilityStatus {

	AVAILABLE(0), DEAL_SEALED(1), NOT_AVAILABLE(2);

	private int value;

	private PropertyAvailabilityStatus(Integer value) {
		this.value = value;
	}

	public static PropertyAvailabilityStatus from(int value) {

		switch (value) {

		case 0:
			return PropertyAvailabilityStatus.AVAILABLE;

		case 1:
			return PropertyAvailabilityStatus.DEAL_SEALED;
 
		case 2:
			return PropertyAvailabilityStatus.NOT_AVAILABLE;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
