package com.re.paas.integrated.models;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.classes.ThreadContext;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.Model;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.utils.LocaleUtils;

@Model(dependencies = ConfigModel.class)
public class LocaleModel extends BaseModel {

	private static String defaultLocale;

	public static final String USER_DEFAULT_LOCALE = "USER_DEFAULT_LOCALE";
	public static final String USER_DEFAULT_NUMBER_FORMAT = "USER_DEFAULT_NUMBER_FORMAT";

	@Override
	public String path() {
		return "core/locale";
	}

	@Override
	public void install(InstallOptions options) {

		Locale locale = Locale
				.forLanguageTag(LocaleUtils.buildLocaleString(options.getLanguage(), options.getCountry()));
		ConfigModel.putString(ConfigKeys.DEFAULT_LOCALE, locale.toLanguageTag());
	}

	@Override
	public void start() {
		defaultLocale = ConfigModel.get(ConfigKeys.DEFAULT_LOCALE);
	}

	protected static String getDefaultLocale() {
		return defaultLocale;
	}

	public static String defaultLocale() {
		return defaultLocale != null ? defaultLocale : 
			//The platform may not have been installed
			"en-US";
	}

	protected static String defaultTimezone() {
		return ConfigModel.get(ConfigKeys.DEFAULT_TIMEZONE);
	}

	protected static void setDefaultLocale(Locale locale) {
		ConfigModel.putString(ConfigKeys.DEFAULT_LOCALE, locale.toString());
	}

	@PlatformInternal
	public static void setUserLocale(List<String> acceptableLocales, Long principal) {

		String defaultLocal = !acceptableLocales.isEmpty() ? acceptableLocales.get(0) : null;

		if (defaultLocal == null && principal != null) {
			defaultLocal = BaseUserModel.getPreferredLocale(principal);
		} else {
			defaultLocal = LocaleModel.defaultLocale();
		}

		Locale locale = Locale.forLanguageTag(defaultLocal);

		ThreadContext.set(USER_DEFAULT_LOCALE, locale.toLanguageTag());
		ThreadContext.set(USER_DEFAULT_NUMBER_FORMAT, NumberFormat.getCurrencyInstance(locale));
	}

	protected static String getUserLocale() {
		return ThreadContext.isRequestContext() ? ThreadContext.get(USER_DEFAULT_LOCALE).toString() : defaultLocale();
	}

	protected static NumberFormat getUserNumberFormat() {
		return (NumberFormat) ThreadContext.get(USER_DEFAULT_NUMBER_FORMAT);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}
}
