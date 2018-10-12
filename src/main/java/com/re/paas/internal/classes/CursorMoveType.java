package com.re.paas.internal.classes;

public enum CursorMoveType {

	PREVIOUS(0), NEXT(1);

	private int value;

	private CursorMoveType(Integer value) {
		this.value = value;
	}

	public static CursorMoveType from(int value) {

		switch (value) {

		case 0:
			return CursorMoveType.PREVIOUS;
			
		case 1:
			return CursorMoveType.NEXT;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
