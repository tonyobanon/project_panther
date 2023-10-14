package com.re.paas.api.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.re.paas.api.classes.Exceptions;

public class SerializationUtil {
	
	private static DateFormat SDF;
	
	public static String toDateString(Date date) {
		return SDF.format(date);
	}
	
//	public static Date fromDateString(String dateString) {
//		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//
//		OffsetDateTime odt = OffsetDateTime.parse(dateString, formatter);
//		Instant instant = odt.toInstant();
//		Date date = Date.from(instant);
//		
//		return date;
//	}
	
//	try {
//		DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
//        Instant instant = Instant.from(dtf.parse(value));
//        Date date = Date.from(instant);
//        
//	} catch (DateTimeParseException e) {
//		// Ignore the exception and return the original value
//	}

	public static Date fromDateString(String dateString) {
          try {
			return SDF.parse(dateString);
		} catch (ParseException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}
	
	static {
		SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SDF.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
}
