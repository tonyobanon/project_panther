package com.re.paas.internal.utils;

import java.util.Locale;

public class LocaleUtils {

	public static final String LANGUAGE_COUNTRY_DELIMETER = "-";
	
	public static String buildLocaleString(String language, String country) {
		return language + LANGUAGE_COUNTRY_DELIMETER + country;
	}
	
	public static String buildLocaleString(Locale locale) {
		return buildLocaleString(locale.getLanguage(), locale.getCountry());
	}
	
}
