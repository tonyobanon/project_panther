package com.re.paas.internal.classes.spec;

public enum ClientSignatureType {
	
	IS_NEW_CUSTOMER(0, 2), IS_PRICE_CONSCIOUS(1, 3), HAS_VIEWED_PRICE_GRAPH(2, 4),
	ORIGIN_CITY(3, 3), ORIGIN_TERRITORY(4, 2), ORIGIN_COUNTRY(5, 1);

	private int score;
	private int value;

	private ClientSignatureType(Integer value, int score) {
		this.score = score;
		this.value = value;
	}

	public static ClientSignatureType from(int value) {

		switch (value) {

		case 0:
			return ClientSignatureType.IS_NEW_CUSTOMER;
		case 1:
			return ClientSignatureType.IS_PRICE_CONSCIOUS;
		case 2:
			return ClientSignatureType.HAS_VIEWED_PRICE_GRAPH;
		case 3:
			return ClientSignatureType.ORIGIN_CITY;
		case 4:
			return ClientSignatureType.ORIGIN_TERRITORY;
		case 5:
			return ClientSignatureType.ORIGIN_COUNTRY;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getScore() {
		return score;
	}

	public int getValue() {
		return value;
	}
}
