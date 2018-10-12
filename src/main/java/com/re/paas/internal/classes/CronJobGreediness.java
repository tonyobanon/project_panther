package com.re.paas.internal.classes;

public enum CronJobGreediness {

	RELUNCTANT(0), GREEDY(1);

	private int value;

	private CronJobGreediness(Integer value) {
		this.value = value;
	}

	public static CronJobGreediness from(int value) {

		switch (value) {

		case 0:
			return CronJobGreediness.RELUNCTANT;
			
		case 1:
			return CronJobGreediness.GREEDY;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}
}
