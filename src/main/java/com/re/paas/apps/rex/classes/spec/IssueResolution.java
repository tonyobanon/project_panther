package com.re.paas.apps.rex.classes.spec;

public enum IssueResolution {

	OPEN(0), ACKNOWLEDGED(1), RESOLVED(2), CLOSED(3);

	private int value;

	private IssueResolution(Integer value) {
		this.value = value;
	}

	public static IssueResolution from(int value) {

		switch (value) {

		case 0:
			return IssueResolution.OPEN;

		case 1:
			return IssueResolution.ACKNOWLEDGED;

		case 2:
			return IssueResolution.RESOLVED;
			
		case 3:
			return IssueResolution.CLOSED;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
