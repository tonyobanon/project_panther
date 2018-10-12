package com.re.paas.internal.classes;

public enum CronType {

	STANDALONE_JOB(0), MODEL_TASK(1);

	private int value;

	private CronType(Integer value) {
		this.value = value;
	}

	public static CronType from(int value) {

		switch (value) {

		case 0:
			return CronType.STANDALONE_JOB;
			
		case 1:
			return CronType.MODEL_TASK;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
