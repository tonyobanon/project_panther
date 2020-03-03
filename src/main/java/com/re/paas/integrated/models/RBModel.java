package com.re.paas.integrated.models;


import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.re.paas.api.annotations.develop.BlockerBlockerTodo;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.models.classes.RBEntry;
import com.re.paas.api.utils.Dates;
import com.re.paas.integrated.fusion.functionalities.RoleFunctionalities;
import com.re.paas.integrated.models.helpers.CacheHelper;
import com.re.paas.integrated.tables.defs.base.RBEntryTable;
import com.re.paas.internal.caching.CacheType;
import com.re.paas.internal.classes.ResourceScanner;
import com.re.paas.internal.utils.LocaleUtils;

public class RBModel extends BaseModel {

	private static final boolean STORE_IN_MEMORY = true;

	private static final Map<String, Map<String, Object>> entries = new HashMap<>();

	private static final CacheType defaultCacheType = CacheType.PERSISTENT;

	private static String ext = ".properties";
	private static Pattern pattern = Pattern.compile("_[a-z]{2,3}[[-]a-zA-Z]{2,3}\\Q.properties\\E\\z");

	// K: Language, V: CountryCode ...
	private static final Map<String, List<String>> localeCountries = new HashMap<>();

	private static final Map<String, String> alternateCountries = new HashMap<>();

	private static final String BUNDLE_PREFIX = "rb_";

	@Override
	public String path() {
		return "core/resource_bundle";
	}

	@Override
	public void preInstall() {
		start();
	}

	@Override
	@BlockerTodo("Start cron that performs auto translation ")
	public void install(InstallOptions options) {

		// Start cron that performs auto translation of all modules into all languages
		// and store each bundle in a properties file

	}

	@Todo("Make RB entries persistent, add support for auto translation via google translate")
	@Override
	public void start() {

		// Read resource bundle files
		new ResourceScanner(ext).scan().forEach(p -> {

			boolean isBundle = false;
			String filePath = p.toString();

			Matcher m = pattern.matcher(filePath);

			while (m.find()) {

				isBundle = true;

				String match = m.group();

				Logger.get().debug("Saving resource bundle: " + p.getFileName());

				// String bundleName = p.getFileName().toString().replace(match, "");

				String localeString = match.replace("_", "").replace(".properties", "");

				Locale locale = Locale.forLanguageTag(localeString);
				addLocale(locale);

				Properties bundle = new Properties();
				try {
					bundle.load(Files.newInputStream(p));
				} catch (IOException e) {
					Exceptions.throwRuntime(e);
				}

				Map<String, Object> entries = new HashMap<>();

				// Add To Cache
				bundle.forEach((k, v) -> {

					String key = checkKey(k.toString());
					entries.put(key, v);
				});

				putAll(localeString, entries);
			}

			if (!isBundle) {
				// Logger.warn(filePath + " could not be identified as a resource bundle");
			}
		});

		Map<String, Map<String, Object>> iEntries = new HashMap<>();

		
		// Read from DB
		ofy().load().type(RBEntryTable.class).forEach(e -> {

			if (!iEntries.containsKey(e.getLocale())) {
				iEntries.putString(e.getLocale(), new HashMap<>());
			}

			iEntries.forEach(e.getLocale()).putString(e.getKey(), e.getValue());
		});

		iEntries.forEach((k, v) -> {
			putAll(k, v);
		});
	}

	protected static List<String> availableCountries() {
		List<String> result = Lists.newArrayList();
		localeCountries.values().forEach(v -> {
			result.addAll(v);
		});
		return result;
	}
	
	protected static List<String> getSupportedLanguage() {
		List<String> result = Lists.newArrayList();
		localeCountries.values().forEach(v -> {
			result.addAll(v);
		});
		return result;
	}
	
	protected static void newEntry(RBEntry... entries) {

		Map<String, Map<String, Object>> iEntries = new HashMap<>();

		List<RBEntryTable> entities = new ArrayList<>();

		for (RBEntry entry : entries) {

			RBEntryTable e = new RBEntryTable().setKey(entry.getKey()).setLocale(entry.getLocale())
					.setValue(entry.getValue()).setDateCreated(Dates.now());
			entities.add(e);

			if (!iEntries.containsKey(entry.getLocale())) {
				iEntries.put(entry.getLocale(), new HashMap<>());
			}

			iEntries.get(entry.getLocale()).put(entry.getKey(), entry.getValue());
		}

		ofy().save().entities(entities);

		String defaultLocale = LocaleModel.defaultLocale();
		Map<String, Object> result = new HashMap<>();

		iEntries.forEach((locale, localeEntries) -> {

			putAll(locale, localeEntries);

			if (locale.equals(defaultLocale)) {
				return;
			}

			// Consolidate, by translating for default locale

			localeEntries.forEach((key, value) -> {

				// K: value, V: key
				final Map<String, String> vMapping = new HashMap<>();

				if (_get(defaultLocale, key) == null) {
					vMapping.put(value.toString(), key);
				}

				Map<String, String> tResult = TranslateModel.translate(Lists.newArrayList(vMapping.keySet()), locale,
						defaultLocale);

				tResult.forEach((k, v) -> {
					result.put(vMapping.get(k), v);
				});
			});
		});

		putAll(defaultLocale, result);
	}

	public Map<String, Object> getAll(String locale) {
		if (!STORE_IN_MEMORY) {
			return CacheHelper.getMap(defaultCacheType, getKey(locale));
		} else {
			return entries.get(getKey(locale));
		}
	}

	private static void putAll(String locale, Map<String, Object> values) {
		String key = getKey(locale);

		if (!STORE_IN_MEMORY) {
			CacheHelper.addToMapOrCreate(defaultCacheType, key, values);
		} else {
			Map<String, Object> valuesMap = entries.get(key);
			if (valuesMap == null) {
				entries.put(key, values);
			} else {
				valuesMap.putAll(values);
			}
		}
	}

	public static String _get(String localeString, String key) {
		if (!STORE_IN_MEMORY) {
			Object value = CacheHelper.getMapEntry(defaultCacheType, getKey(localeString), key);
			return value != null ? value.toString() : null;
		} else {
			Object value = entries.get(getKey(localeString)).get(key);
			return value != null ? value.toString() : null;
		}
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.GET_RESOURCE_BUNDLE_ENTRIES)
	public static String get(String key) {
		return get(key, new HashMap<>());
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_RESOURCE_BUNDLE_ENTRIES)
	@BlockerBlockerTodo("When translating, take into consideration, whether its's an email, phone number, e.t.c")
	public static String get(String key, Map<String, Object> variables) {

		// If this is called outside request context, then null will be returned

		String userLocale = LocaleModel.getUserLocale();
		String defaultLocale = LocaleModel.defaultLocale();

		String value = get(userLocale != null ? userLocale : defaultLocale, key);
		if (value == null) {

			if (!userLocale.equals(defaultLocale)) {

				// Use the system's default locale instead
				value = get(defaultLocale, key);

				if (value == null) {
					throw new NullPointerException("No Resource Bundle entry for key: " + key);
				}

				// Since the value is available using the platform's default locale, translate
				// it, store and return it
				String nValue = TranslateModel.translate(FluentArrayList.asList(key), defaultLocale, userLocale)
						.get(key);

				newEntry(new RBEntry(key, userLocale, nValue));

				value = nValue;

			} else {
				throw new NullPointerException("No Resource Bundle entry for key: " + key);
			}
		}

		return addVariables(value, variables);
	}

	public static String get(String localeString, String key) {
		return get(localeString, key, null);
	}

	public static String get(String localeString, String key, Map<String, Object> variables) {

		String _key = key;
		key = checkKey(key);

		Object value = _get(localeString, key);

		if (value == null) {

			Locale locale = Locale.forLanguageTag(localeString);

			// Use the alternative country, if any

			if (alternateCountries.containsKey(locale.getCountry())) {

				return get(LocaleUtils.buildLocaleString(locale.getLanguage(),
						alternateCountries.get(locale.getCountry())), key);

			} else if (localeCountries.containsKey(locale.getLanguage())) {

				// From all available countries, find a suitable

				for (String country : localeCountries.get(locale.getLanguage())) {

					value = _get(LocaleUtils.buildLocaleString(locale.getLanguage(), country), key);

					if (value != null) {

						if (alternateCountries.containsKey(country)
								&& alternateCountries.get(country).equals(locale.getCountry())) {

							// <locale.getCountry()> is already the alternate country of <country>
							// Don't set as alternate country, to avoid a StackOverflowException happening
							// in the future

						} else {

							// Set as alternate country
							alternateCountries.put(locale.getCountry(), country);
						}

						break;
					}
				}
			}
		}

		if (Character.isUpperCase(_key.charAt(0)) && value != null) {
			String _value = value.toString();
			String fw = _value.substring(0, 1);
			value = _value.replaceFirst(Pattern.quote(fw), fw.toUpperCase());
		}

		return value != null ? addVariables(value.toString(), variables) : null;
	}

	private static String addVariables(String in, Map<String, Object> variables) {
		if (variables == null || variables.isEmpty()) {
			return in;
		}
		for (Entry<String, Object> variable : variables.entrySet()) {
			in = in.replaceAll(Pattern.quote("$" + variable.getKey()), variable.getValue().toString());
		}

		return in;
	}

	private static String getKey(String locale) {
		return BUNDLE_PREFIX + locale;
	}

	private static String checkKey(String key) {

		if (key == null | key.trim().equals("")) {
			throw new NullPointerException();
		}

		return key.toLowerCase();
	}

	private static void addLocale(Locale l) {
		if (!localeCountries.containsKey(l.getLanguage())) {
			localeCountries.put(l.getLanguage(), new ArrayList<>());
		}
		localeCountries.get(l.getLanguage()).add(l.getCountry());
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_AVAILABLE_COUNTRIES)
	public static Map<String, List<String>> getLocaleCountries() {
		return localeCountries;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
