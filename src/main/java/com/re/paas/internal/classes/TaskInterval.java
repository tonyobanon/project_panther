package com.re.paas.internal.classes;

public enum TaskInterval {

	ONE_MINUTE(0, 1), FIVE_MINUTES(1, 5), FIFTEEN_MINUTES(2, 15), THIRTY_MINUTES(3, 30), ONE_HOURLY(4, 60), SIX_HOURLY(
			5, 360), TWELVE_HOURLY(6, 720), EVERY_DAY(7, 1440), EVERY_TWO_DAYS(8, 2880,
					CronJobGreediness.GREEDY), WEEKLY(9, 10080, CronJobGreediness.GREEDY),
					MONTHLY(10, 40320, CronJobGreediness.GREEDY);

	private final int value;
	private final int minOffset;
	private final CronJobGreediness greediness;

	private TaskInterval(Integer value, int hourOffset) {
		this(value, hourOffset, CronJobGreediness.RELUNCTANT);
	}

	private TaskInterval(Integer value, int minOffset, CronJobGreediness greediness) {
		this.value = value;
		this.minOffset = minOffset;
		this.greediness = greediness;
	}

	public static TaskInterval from(int value) {

		switch (value) {

		case 0:
			return TaskInterval.ONE_MINUTE;
		case 1:
			return TaskInterval.FIVE_MINUTES;
		case 2:
			return TaskInterval.FIFTEEN_MINUTES;
		case 3:
			return TaskInterval.THIRTY_MINUTES;
		case 4:
			return TaskInterval.ONE_HOURLY;
		case 5:
			return TaskInterval.SIX_HOURLY;
		case 6:
			return TaskInterval.TWELVE_HOURLY;
		case 7:
			return TaskInterval.EVERY_DAY;
		case 8:
			return TaskInterval.EVERY_TWO_DAYS;
		case 9:
			return TaskInterval.WEEKLY;
		case 10:
			return TaskInterval.MONTHLY;
			
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
