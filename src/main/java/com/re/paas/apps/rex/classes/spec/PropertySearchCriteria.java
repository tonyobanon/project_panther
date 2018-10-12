package com.re.paas.apps.rex.classes.spec;

public enum PropertySearchCriteria {

	BY_CITY(0), BY_AGENT_ORGANIZATION(1), ADVANCED_SEARCH(2);

	private int value;

	private PropertySearchCriteria(Integer value) {
		this.value = value;
	}

	public static PropertySearchCriteria from(int value) {

		switch (value) {

		case 0:
			return PropertySearchCriteria.BY_CITY;

		case 1:
			return PropertySearchCriteria.BY_AGENT_ORGANIZATION;

		case 2:
			return PropertySearchCriteria.ADVANCED_SEARCH;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
