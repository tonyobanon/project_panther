package com.re.paas.apps.rex.calendar_schedule;

import java.util.ArrayList;
import java.util.List;

public class CalendarMonth {

	private Integer value;
	private String name;
	
	private List<CalendarDay> days = new ArrayList<>();

	
	public CalendarMonth(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public CalendarMonth setValue(Integer value) {
		this.value = value;
		return this;
	}

	public String getName() {
		return name;
	}

	public CalendarMonth setName(String name) {
		this.name = name;
		return this;
	}

	public List<CalendarDay> getDays() {
		return days;
	}

	public CalendarMonth addDay(CalendarDay day) {
		this.days.add(day);
		return this;
	}
	
	public CalendarMonth setDays(List<CalendarDay> days) {
		this.days = days;
		return this;
	}
	
}
