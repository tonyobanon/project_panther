package com.re.paas.api.listable;

public enum ListingType {

	LIST(0), SEARCH(1);

	private int value;

	private ListingType(Integer value) {
		this.value = value;
	}

	public static ListingType from(int value) {

		switch (value) {

		case 0:
			return ListingType.LIST;
			
		case 1:
			return ListingType.SEARCH;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
