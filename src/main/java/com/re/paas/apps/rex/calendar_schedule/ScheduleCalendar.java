package com.re.paas.apps.rex.calendar_schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCalendar {

	List<CalendarMonth> months = new ArrayList<>();

	public List<CalendarMonth> getMonths() {
		return months;
	}

	public ScheduleCalendar addMonth(CalendarMonth month) {
		this.months.add(month);
		return this;
	}
	
	public ScheduleCalendar setMonths(List<CalendarMonth> months) {
		this.months = months;
		return this;
	}
	
}
