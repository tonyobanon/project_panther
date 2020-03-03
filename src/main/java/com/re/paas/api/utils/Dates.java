package com.re.paas.api.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;

@BlockerTodo("Here and global.js, stop using the system's default timezone offset. Instead use platform configured timezone")
public class Dates {

	private static DateFormat prettyFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	@BlockerTodo("Save using the platform's default timezone")
	public static Date now() {
		return new Date();
	}

	public static Calendar getCalendar() {
		return Calendar.getInstance();
	}

	public static String currentDate() {
		return format.format(getCalendar().getTime());
	}

	public static String toString(Date o) {
		return format.format(o);
	}

	public static String toString(String startDate, Long incrValue, TemporalUnit unit) {
		return toString(toDate(startDate), incrValue, unit);
	}

	public static String toString(Date startDate, Long incrValue, TemporalUnit unit) {

		Instant later = startDate.toInstant().plus(incrValue, unit);

		String dateString = Dates.toString(Date.from(later));

		return dateString;
	}

	public static String toPrettyString(Date o) {
		return prettyFormat.format(o);
	}

	public static Date toDate(String o) {
		try {
			return format.parse(o);
		} catch (ParseException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	public static Instant getInstant() {
		return now().toInstant();
	}

	public static Instant getNearestDate(Instant date, Instant... dates) {
		long minDiff = -1, currentTime = date.get(ChronoField.INSTANT_SECONDS);
		Instant minDate = null;
		for (Instant i : dates) {
			long diff = Math.abs(currentTime - i.get(ChronoField.INSTANT_SECONDS));
			if ((minDiff == -1) || (diff < minDiff)) {
				minDiff = diff;
				minDate = i;
			}
		}
		return minDate;
	}
}
