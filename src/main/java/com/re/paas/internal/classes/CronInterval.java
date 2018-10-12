package com.re.paas.internal.classes;

public enum CronInterval {

	ONE_MINUTE(0, 1), FIVE_MINUTES(1, 5), FIFTEEN_MINUTES(2, 15), THIRTY_MINUTES(3, 30), ONE_HOURLY(4, 60), SIX_HOURLY(
			5, 360), TWELVE_HOURLY(6, 720), EVERY_DAY(7, 1440), EVERY_TWO_DAYS(8, 2880,
					CronJobGreediness.GREEDY), WEEKLY(9, 10080, CronJobGreediness.GREEDY),
					MONTHLY(10, 40320, CronJobGreediness.GREEDY);

	private final int value;
	private final int minOffset;
	private final CronJobGreediness greediness;

	private CronInterval(Integer value, int hourOffset) {
		this(value, hourOffset, CronJobGreediness.RELUNCTANT);
	}

	private CronInterval(Integer value, int minOffset, CronJobGreediness greediness) {
		this.value = value;
		this.minOffset = minOffset;
		this.greediness = greediness;
	}

	public static CronInterval from(int value) {

		switch (value) {

		case 0:
			return CronInterval.ONE_MINUTE;
		case 1:
			return CronInterval.FIVE_MINUTES;
		case 2:
			return CronInterval.FIFTEEN_MINUTES;
		case 3:
			return CronInterval.THIRTY_MINUTES;
		case 4:
			return CronInterval.ONE_HOURLY;
		case 5:
			return CronInterval.SIX_HOURLY;
		case 6:
			return CronInterval.TWELVE_HOURLY;
		case 7:
			return CronInterval.EVERY_DAY;
		case 8:
			return CronInterval.EVERY_TWO_DAYS;
		case 9:
			return CronInterval.WEEKLY;
		case 10:
			return CronInterval.MONTHLY;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public int getValue() {
		return value;
	}

	public int getMinutesOffset() {
		return minOffset;
	}

	public CronJobGreediness getGreediness() {
		return greediness;
	}
}
