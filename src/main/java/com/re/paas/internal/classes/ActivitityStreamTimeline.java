package com.re.paas.internal.classes;

public enum ActivitityStreamTimeline {

	HOURLY(1), DAILY(2), WEEKLY(3);

	private int value;

	private ActivitityStreamTimeline(Integer value) {
		this.value = value;
	}

	public static ActivitityStreamTimeline from(int value) {

		switch (value) {

		case 1:
			return ActivitityStreamTimeline.HOURLY;
			
		case 2:
			return ActivitityStreamTimeline.DAILY;
			
		case 3:
			return ActivitityStreamTimeline.WEEKLY;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public Integer getValue() {
		return value;
	}
}
