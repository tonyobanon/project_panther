package com.re.paas.internal.locations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.WriteRequest;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.fusion.functionalities.LocationFunctionalities;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.fusion.services.impl.Unexposed;
import com.re.paas.internal.locations.geonames.Admin1CodesFeatures;
import com.re.paas.internal.locations.geonames.Cities1000Features;
import com.re.paas.internal.locations.geonames.Coordinates;
import com.re.paas.internal.locations.geonames.CountryInfoFeatures;
import com.re.paas.internal.runtime.spi.ClassLoaders;
import com.re.paas.internal.tables.defs.locations.CityTable;
import com.re.paas.internal.tables.defs.locations.CountryTable;
import com.re.paas.internal.tables.defs.locations.LanguageTable;
import com.re.paas.internal.tables.defs.locations.TerritoryTable;
import com.re.paas.internal.tables.spec.locations.CityTableSpec;
import com.re.paas.internal.tables.spec.locations.CountryTableSpec;
import com.re.paas.internal.tables.spec.locations.LanguageTableSpec;
import com.re.paas.internal.tables.spec.locations.TerritoryTableSpec;

public class LocationModel extends BaseModel {

	private static Path baseLocationDataPath = ClassLoaders.getClassPath().resolve("location_data");

	private final static Path ADMIN1_CODES_FILE = baseLocationDataPath.resolve("admin1CodesASCII.txt");

	private final static Path CITIES_1000_FILE = baseLocationDataPath.resolve("cities1000.txt");

	private final static Path COUNTRY_INFO_FILE = baseLocationDataPath.resolve("countryInfo.txt");

	private final static Path LANGUAGE_CODES_FILE = baseLocationDataPath.resolve("languageCodes2.txt");

	@Override
	public String path() {
		return "core/location";
	}

	@Override
	public void preInstall() {

		// Load Countries
		loadCountries();

		// Load Territories
		loadTerritories(null);

		// Load Cities
		loadCities(null);

		// Load Languages
		loadLanguages();
	}

	public void install(InstallOptions options) {

	}

	@Override
	public void start() {

	}

	@Todo("Use java.util.Collections.sort to arrange alphabetically")
	private static void loadCountries() {

		Logger.get().info("Loading countries data from resource file");

		BufferedReader buffer;
		String line = null;

		try {

			buffer = new BufferedReader(
					new InputStreamReader(Files.newInputStream(COUNTRY_INFO_FILE), Charset.forName("UTF-8")));

			BatchWriteItemRequest writes = new BatchWriteItemRequest();

			while ((line = buffer.readLine()) != null) {

				if (line.startsWith("#")) {
					continue;
				}

				String[] values = line.split("\t");

				Item i = new Item();

				i.with(CountryTableSpec.CODE, values[CountryInfoFeatures.ISO_CODE])
						.with(CountryTableSpec.COUNTRY_NAME, values[CountryInfoFeatures.COUNTRY])
						.with(CountryTableSpec.CURRENCY_CODE, values[CountryInfoFeatures.CURRENCY_CODE])
						.with(CountryTableSpec.CURRENCY_NAME, values[CountryInfoFeatures.CURRENCY_NAME])

						.with(CountryTableSpec.LANGUAGE_CODE,
								values[CountryInfoFeatures.LANGUAGES].split(Pattern.quote(","))[0])
						.withStringSet(CountryTableSpec.SPOKEN_LANGUAGES,
								values[CountryInfoFeatures.LANGUAGES].split(Pattern.quote(",")))

						.with(CountryTableSpec.DIALING_CODE, values[CountryInfoFeatures.PHONE_DIALING_CODE]);

				writes.addRequestItem(CountryTable.class, new WriteRequest(i));
			}

			Database.get().batchWriteItem(writes);

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	private static void loadTerritories(String countryCode) {

		Logger.get().info("Loading territories data from resource file");

		BufferedReader buffer;
		String line = null;

		try {

			BatchWriteItemRequest writes = new BatchWriteItemRequest();

			buffer = new BufferedReader(
					new InputStreamReader(Files.newInputStream(ADMIN1_CODES_FILE), Charset.forName("UTF-8")));

			while ((line = buffer.readLine()) != null) {

				if (line.startsWith("#")) {
					continue;
				}

				String[] values = line.split("\t");

				if (countryCode != null && !values[Admin1CodesFeatures.TERRITORY_CODE].split(Pattern.quote("."))[0]
						.equals(countryCode)) {
					continue;
				}

				Item i = new Item().with(TerritoryTableSpec.CODE, values[Admin1CodesFeatures.TERRITORY_CODE])
						.with(TerritoryTableSpec.TERRITORY_NAME, values[Admin1CodesFeatures.NAME])
						.with(TerritoryTableSpec.COUNTRY_CODE,
								values[Admin1CodesFeatures.TERRITORY_CODE].split(Pattern.quote("."))[0]);

				writes.addRequestItem(TerritoryTable.class, new WriteRequest(i));
			}

			Database.get().batchWriteItem(writes);

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

	}

	private static void loadCities(String countryCode) {

		Logger.get().info("Loading cities data from resource file");

		BufferedReader buffer;
		String line = null;

		try {

			BatchWriteItemRequest writes = new BatchWriteItemRequest();

			buffer = new BufferedReader(
					new InputStreamReader(Files.newInputStream(CITIES_1000_FILE), Charset.forName("UTF-8")));

			while ((line = buffer.readLine()) != null) {

				if (line.startsWith("#")) {
					continue;
				}

				String[] values = line.split("\t");

				if (countryCode != null && !values[Cities1000Features.COUNTRY_CODE].equals(countryCode)) {
					continue;
				}

				Item i = new Item().with(CityTableSpec.ID, values[Cities1000Features.GEONAMEID])
						.with(CityTableSpec.NAME, values[Cities1000Features.NAME])
						.with(CityTableSpec.TERRITOTY_CODE,
								values[Cities1000Features.COUNTRY_CODE] + "." + values[Cities1000Features.ADMIN1])
						.with(CityTableSpec.TIMEZONE, values[Cities1000Features.TIMEZONE])
						.with(CityTableSpec.LATITUDE, Double.parseDouble(values[Cities1000Features.LATITUDE]))
						.with(CityTableSpec.LONGITUDE, Double.parseDouble(values[Cities1000Features.LONGITUDE]));

				writes.addRequestItem(CityTable.class, new WriteRequest(i));
			}

			Database.get().batchWriteItem(writes);

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	private static void loadLanguages() {

		Logger.get().info("Loading languages data from resource file");

		BufferedReader buffer;
		String line = null;

		try {

			BatchWriteItemRequest writes = new BatchWriteItemRequest();

			buffer = new BufferedReader(
					new InputStreamReader(Files.newInputStream(LANGUAGE_CODES_FILE), Charset.forName("UTF-8")));

			while ((line = buffer.readLine()) != null) {

				if (line.startsWith("#")) {
					continue;
				}

				String[] values = line.split(",");

				Item i = new Item().with(LanguageTableSpec.CODE, values[0].replace("\"", ""))
						.with(LanguageTableSpec.LANG_NAME, values[1].replace("\"", ""));

				writes.addRequestItem(LanguageTable.class, new WriteRequest(i));
			}

			Database.get().batchWriteItem(writes);

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static String getTerritoryName(String territoryCode) {
		return Database.get().getTable(TerritoryTable.class)
				.getItem(GetItemSpec.forKey(TerritoryTableSpec.CODE, territoryCode, TerritoryTableSpec.TERRITORY_NAME))
				.getString(TerritoryTableSpec.TERRITORY_NAME);
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static String getCountryName(String countryCode) {
		return Database.get().getTable(CountryTable.class)
				.getItem(GetItemSpec.forKey(CountryTableSpec.CODE, countryCode, CountryTableSpec.COUNTRY_NAME))
				.getString(CountryTableSpec.COUNTRY_NAME);
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static String getCountryDialingCode(String countryCode) {
		return Database.get().getTable(CountryTable.class)
				.getItem(GetItemSpec.forKey(CountryTableSpec.CODE, countryCode, CountryTableSpec.DIALING_CODE))
				.getString(CountryTableSpec.DIALING_CODE);
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getCountryNames() {
		return getCountryNames(Collections.emptyList());
	}

	@Unexposed
	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getCountryNames(List<String> countryCodes) {

		Map<String, String> result = new HashMap<>(countryCodes.size());

		if (!countryCodes.isEmpty()) {

			// Perform a batch get request

			BatchGetItemRequest request = new BatchGetItemRequest();

			request.addRequestItem(CountryTable.class,
					GetItemsSpec.forKeys(countryCodes.stream().map(code -> new PrimaryKey(CountryTableSpec.CODE, code))
							.collect(Collectors.toList()), CountryTableSpec.COUNTRY_NAME));

			Database.get().batchGetItem(request).getResponses(CountryTable.class).forEach(i -> {
				result.put(i.getString(CountryTableSpec.CODE), CountryTableSpec.COUNTRY_NAME);
			});

		} else {

			// Do a table scan

			ScanSpec spec = ExpressionSpecBuilder
					.addProjections(new ExpressionSpecBuilder(), CountryTableSpec.COUNTRY_NAME).buildForScan();
			Database.get().getTable(CountryTable.class).scan(spec).forEach(r -> {
				r.getItems().forEach(i -> {
					result.put(i.getString(CountryTableSpec.CODE), CountryTableSpec.COUNTRY_NAME);
				});
			});
		}

		return result;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static String getCurrencyCode(String countryCode) {
		return Database.get().getTable(CountryTable.class)
				.getItem(GetItemSpec.forKey(CountryTableSpec.CODE, countryCode, CountryTableSpec.CURRENCY_CODE))
				.getString(CountryTableSpec.CURRENCY_CODE);
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getCurrencyNames() {
		return getCurrencyNames(Collections.emptyList());
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getCurrencyNames(List<String> currencyCodes) {

		Map<String, String> result = new HashMap<>(currencyCodes.size());

		if (!currencyCodes.isEmpty()) {

			// Perform a query
			Index currencyIndex = Database.get().getTable(CountryTable.class).getIndex(CountryTableSpec.CURRENCY_INDEX);
			currencyIndex.all(CountryTableSpec.CURRENCY_CODE, currencyCodes.toArray(new String[currencyCodes.size()]),
					CountryTableSpec.CURRENCY_NAME).forEach(i -> {
						result.put(i.getString(CountryTableSpec.CURRENCY_CODE), CountryTableSpec.CURRENCY_NAME);
					});

		} else {

			// Do a table scan

			ScanSpec spec = new ExpressionSpecBuilder()
					.addProjection(CountryTableSpec.CURRENCY_CODE, CountryTableSpec.CURRENCY_NAME).buildForScan();
			Database.get().getTable(CountryTable.class).scan(spec).forEach(r -> {
				r.getItems().forEach(i -> {
					result.put(i.getString(CountryTableSpec.CURRENCY_CODE), CountryTableSpec.CURRENCY_NAME);
				});
			});
		}

		return result;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static String getCityName(Integer cityId) {
		return Database.get().getTable(CityTable.class)
				.getItem(GetItemSpec.forKey(CityTableSpec.ID, cityId, CityTableSpec.NAME))
				.getString(CityTableSpec.NAME);
	}

	@BlockerTodo
	public static Integer getPostalCode(Integer cityId) {
		return 0;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getAvailableLocales(String countryCode) {
		Map<String, String> locales = new HashMap<>();
		for (Locale o : Locale.getAvailableLocales()) {
			if (o.getCountry().equals(countryCode)) {
				locales.put(o.toLanguageTag(), o.getDisplayName());
			}
		}
		return locales;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getAllLocales() {
		Map<String, String> locales = new HashMap<>();
		for (Locale o : Locale.getAvailableLocales()) {
			locales.put(o.toLanguageTag(), o.getDisplayName());
		}
		return locales;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getAllTimezones() {
		Map<String, String> timezones = new HashMap<>();
		for (String id : TimeZone.getAvailableIDs()) {
			timezones.put(id, id);
		}
		return timezones;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getLanguageNames(List<String> languageCodes) {

		Map<String, String> result = new HashMap<>(languageCodes.size());
		BatchGetItemRequest request = new BatchGetItemRequest();

		request.addRequestItem(LanguageTable.class,
				GetItemsSpec.forKeys(languageCodes.stream().map(code -> new PrimaryKey(LanguageTableSpec.CODE, code))
						.collect(Collectors.toList()), LanguageTableSpec.LANG_NAME));

		Database.get().batchGetItem(request).getResponses(CountryTable.class).forEach(i -> {
			result.put(i.getString(LanguageTableSpec.CODE), LanguageTableSpec.LANG_NAME);
		});
		return result;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getTerritoryNames(String countryCode) {
		Map<String, String> result = new FluentHashMap<>();

		Database.get().getTable(TerritoryTable.class).getIndex(TerritoryTableSpec.COUNTRY_INDEX)
				.all(TerritoryTableSpec.COUNTRY_CODE, countryCode, TerritoryTableSpec.TERRITORY_NAME,
						TerritoryTableSpec.CODE)
				.forEach(i -> {
					result.put(i.getString(TerritoryTableSpec.CODE), i.getString(TerritoryTableSpec.TERRITORY_NAME));
				});

		return result;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<Integer, String> getCityNames(String territoryCode) {
		Map<Integer, String> result = new FluentHashMap<>();

		Database.get().getTable(CityTable.class).getIndex(CityTableSpec.TERRITOTY_INDEX)
				.all(CityTableSpec.TERRITOTY_CODE, territoryCode, CityTableSpec.NAME, CityTableSpec.ID).forEach(i -> {
					result.put(i.getInt(CityTableSpec.ID), i.getString(CityTableSpec.NAME));
				});

		return result;
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Coordinates getCoordinates(int cityId) {
		Item i = Database.get().getTable(CityTable.class)
				.getItem(GetItemSpec.forKey(CityTableSpec.ID, cityId, CityTableSpec.LATITUDE, CityTableSpec.LONGITUDE));
		return new Coordinates(i.getInt(CityTableSpec.LATITUDE), i.getInt(CityTableSpec.LONGITUDE));
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static Map<String, String> getSpokenLanguages(String countryCode) {

		ArrayList<String> languages = new ArrayList<>();

		Item i = Database.get().getTable(CountryTable.class).getItem(GetItemSpec.forKey(CountryTableSpec.CODE,
				countryCode, CountryTableSpec.LANGUAGE_CODE, CountryTableSpec.SPOKEN_LANGUAGES));

		// First, add the country default language
		String languageCode = Locale.forLanguageTag(i.getString(CountryTableSpec.LANGUAGE_CODE)).getLanguage();
		languages.add(languageCode);

		// Then, add other spoken languages
		e.getSpokenLanguages().forEach(o -> {
			String spokenLanguage = Locale.forLanguageTag(o).getLanguage();
			if (!languages.contains(spokenLanguage)) {
				languages.add(spokenLanguage);
			}
		});

		return getLanguageNames(languages);
	}

	@ModelMethod(functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static String getTimezone(int cityId) {
		return ((CityTable) ofy().load().type(CityTable.class).id(cityId).safe()).getTimezone();
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
