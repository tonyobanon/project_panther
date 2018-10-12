package com.re.paas.internal.classes;

public enum ApplicationStatus {

	CREATED(0), OPEN(1), PENDING(2), DECLINED(3), ACCEPTED(4);

	private int value;

	private ApplicationStatus(Integer value) {
		this.value = value;
	}

	public static ApplicationStatus from(int value) {

		switch (value) {

		case 4:
			return ApplicationStatus.ACCEPTED;
			
		case 3:
			return ApplicationStatus.DECLINED;
			
		case 2:
			return ApplicationStatus.PENDING;
			
		case 1:
			return ApplicationStatus.OPEN;
			
		case 0:
			return ApplicationStatus.CREATED;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public Integer getValue() {
		return value;
	}
}
