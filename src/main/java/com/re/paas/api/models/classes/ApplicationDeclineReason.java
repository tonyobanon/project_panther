package com.re.paas.api.models.classes;

import com.re.paas.api.classes.ClientAware;

public enum ApplicationDeclineReason {

	INCOMPLETE_INFORMATION(0), INCONSISTENT_INFORMATION(1), UNVERIFIED_INFORMATION(2), DUPLICATE(3);

	private Integer value;

	private ApplicationDeclineReason(Integer value) {
		this.value = value;
	}

	public static ApplicationDeclineReason from(int value) {

		switch (value) {

		case 0:
			return ApplicationDeclineReason.INCOMPLETE_INFORMATION;
			
		case 1:
			return ApplicationDeclineReason.INCONSISTENT_INFORMATION;
			
		case 2:
			return ApplicationDeclineReason.UNVERIFIED_INFORMATION;
			
		case 3:
			return ApplicationDeclineReason.DUPLICATE;
			
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	public Integer getValue() {
		return value;
	}
	
	@Override
	@ClientAware
	public String toString() {
		return "application_decline_reason." + value.toString();
	}
}
