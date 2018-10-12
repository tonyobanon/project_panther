package com.re.paas.apps.rex.classes.spec;

public enum PropertyType {

	LUXURY(0), FLAT(1), HOUSE(2), LAND(3), COMMERCIAL(4), EVENT_CENTER(5);

	private int value;

	private PropertyType(Integer value) {
		this.value = value;
	}

	public static PropertyType from(int value) {

		switch (value) {

		case 0:
			return PropertyType.LUXURY;
			
		case 1:
			return PropertyType.FLAT;

		case 2:
			return PropertyType.HOUSE;

		case 3:
			return PropertyType.LAND;
			
		case 4:
			return PropertyType.COMMERCIAL;
			
		case 5:
			return PropertyType.EVENT_CENTER;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
