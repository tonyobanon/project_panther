package com.re.paas.apps.rex.classes.spec;

public enum PropertyListingStatus {

	PENDING_CREATION(0), NEEDS_ATTENTION(1), LIVE(2), PENDING_UPDATE(3), PENDING_DELETION(4);

	private int value;

	private PropertyListingStatus(Integer value) {
		this.value = value;
	}

	public static PropertyListingStatus from(int value) {

		switch (value) {

		case 0:
			return PropertyListingStatus.PENDING_CREATION;

		case 1:
			return PropertyListingStatus.NEEDS_ATTENTION;

		case 2:
			return PropertyListingStatus.LIVE;
			
		case 3:
			return PropertyListingStatus.PENDING_UPDATE;
			
		case 4:
			return PropertyListingStatus.PENDING_DELETION;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
