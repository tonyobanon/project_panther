package com.re.paas.apps.rex.calendar_schedule;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;

public class CalendarDay {

	private String name;
	private boolean isAvailable;
	private ClientRBRef comment;
	private Map<String, String> times = new HashMap<>();

	public String getName() {
		return name;
	}

	public CalendarDay setName(String name) {
		this.name = name;
		return this;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public CalendarDay setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
		return this;
	}

	public ClientRBRef getComment() {
		return comment;
	}

	public CalendarDay setComment(ClientRBRef comment) {
		this.comment = comment;
		return this;
	}

	public Map<String, String> getTimes() {
		return times;
	}
	
	public CalendarDay addTime(String id, String time) {
		this.times.put(id, time);
		return this;
	}

	public CalendarDay setTimes(Map<String, String> times) {
		this.times = times;
		return this;
	}
}
