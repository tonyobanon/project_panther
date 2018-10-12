package com.re.paas.internal.classes;

public enum ActivitityStreamTimeline {

	HOURLY(0), DAILY(1), WEEKLY(2);

	private int value;

	private ActivitityStreamTimeline(Integer value) {
		this.value = value;
	}

	public static ActivitityStreamTimeline from(int value) {

		switch (value) {

		case 0:
			return ActivitityStreamTimeline.HOURLY;
			
		case 1:
			return ActivitityStreamTimeline.DAILY;
			
		case 2:
			return ActivitityStreamTimeline.WEEKLY;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public Integer getValue() {
		return value;
	}
}
