package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.re.paas.api.annotations.Todo;
import com.re.paas.api.annotations.UsesMock;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.internal.classes.AppDirectory;
import com.re.paas.internal.entites.locations.CityEntity;
import com.re.paas.internal.entites.locations.CountryEntity;
import com.re.paas.internal.entites.locations.LanguageEntity;
import com.re.paas.internal.entites.locations.TerritoryEntity;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.fusion.services.impl.Unexposed;
import com.re.paas.internal.geonames.Admin1CodesFeatures;
import com.re.paas.internal.geonames.Cities1000Features;
import com.re.paas.internal.geonames.Coordinates;
import com.re.paas.internal.geonames.CountryInfoFeatures;

@UsesMock
public class LocationModel implements BaseModel {

	// @DEV (Subsets)
	private final static String ADMIN1_CODES_FILE_NAME = "admin1CodesASCII2.txt";

	// @DEV (Subsets)
	private final static String CITIES_1000_FILE_NAME = "cities1001_.txt";

	private final static String COUNTRY_INFO_FILE_NAME = "countryInfo.txt";

	private final static String LANGUAGE_CODES_FILE_NAME = "languageCodes2.txt";

	@Override
	public String path() {
		return "core/location";
	}

	@Override
	public void preInstall() {

		// Load Countries
		saveCountries(loadCountries());

		// Load Territories
		saveTerritories(loadTerritories(null));

		// Load Cities
		saveCities(loadCities(null));

		// Load Languages
		saveLanguages(loadLanguages());
	}

	public void install(InstallOptions options) {

	}

	@Override
	public void start() {

	}

	private static void saveCountries(List<CountryEntity> entities) {
		Logger.get().info("Saving " + entities.size() + " countries");
		ofy().save().entities(entities).now();
	}

	private static void saveTerritories(List<TerritoryEntity> entities) {
		Logger.get().info("Saving " + entities.size() + " territories");
		ofy().save().entities(entities).now();
	}

	private static void saveCities(List<CityEntity> entities) {
		Logger.get().info("Saving " + entities.size() + " cities");
		ofy().save().entities(entities).now();
	}

	private static void saveLanguages(List<LanguageEntity> entities) {
		Logger.get().info("Saving " + entities.size() + " languages");
		ofy().save().entities(entities).now();
	}

	@Todo("Use java.util.Collections.sort to arrange alphabetically")
	private static ArrayList<CountryEntity> loadCountries() {

		Logger.get().info("Loading countries data from resource file");

		ArrayList<CountryEntity> countries = new ArrayList<>();

		BufferedReader buffer;
		String line = null;

		try {

			buffer = new BufferedReader(
					new InputStreamReader(AppDirectory.getInputStream(COUNTRY_INFO_FILE_NAME), Charset.forName("UTF-8")));

			while ((line = buffer.readLine()) != null) {

				if (line.startsWith("#")) {
					continue;
				}

				String[] values = line.split("\t");

				CountryEntity o = new CountryEntity();

				o.setCode((values[CountryInfoFeatures.ISO_CODE]));
				o.setCountryName((values[CountryInfoFeatures.COUNTRY]));
				o.setCurrencyCode((values[CountryInfoFeatures.CURRENCY_CODE]));
				o.setCurrencyName((values[CountryInfoFeatures.CURRENCY_NAME]));

				if (values[CountryInfoFeatures.LANGUAGES].contains(",")) {
					o.setLanguageCode((values[CountryInfoFeatures.LANGUAGES].split(Pattern.quote(","))[0]));
					o.setSpokenLanguages(
							ImmutableList.copyOf((values[CountryInfoFeatures.LANGUAGES].split(Pattern.quote(",")))));
				} else {
					o.setLanguageCode((values[CountryInfoFeatures.LANGUAGES]));
					o.setSpokenLanguages(ImmutableList.copyOf(new String[] { values[CountryInfoFeatures.LANGUAGES] }));
				}

				o.setDialingCode((values[CountryInfoFeatures.PHONE_DIALING_CODE]));

				countries.add(o);
			}
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return countries;
	}

	private static ArrayList<TerritoryEntity> loadTerritories(String countryCode) {

		Logger.get().info("Loading territories data from resource file");

		ArrayList<TerritoryEntity> territories = new ArrayList<>();

		BufferedReader buffer;
		String line = null;

		try {

			buffer = new BufferedReader(
					new InputStreamReader(AppDirectory.getInputStream(ADMIN1_CODES_FILE_NAME), Charset.forName("UTF-8")));

			while ((line = buffer.readLine()) != null) {

				if (line.startsWith("#")) {
					continue;
				}

				String[] values = line.split("\t");

				if (countryCode != null && !values[Admin1CodesFeatures.TERRITORY_CODE].split(Pattern.quote("."))[0]
						.equals(countryCode)) {
					continue;
				}

				TerritoryEntity o = new TerritoryEntity();

				o.setCode(values[Admin1CodesFeatures.TERRITORY_CODE]);
				o.setTerritoryName((values[Admin1CodesFeatures.NAME]));
				o.setCountryCode((values[Admin1CodesFeatures.TERRITORY_CODE].split(Pattern.quote("."))[0]));

				territories.add(o);
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return territories;
	}

	private static ArrayList<CityEntity> loadCities(String countryCode) {

		Logger.get().info("Loading cities data from resource file");

		ArrayList<CityEntity> cities = new ArrayList<>();

		BufferedReader buffer;
		String line = null;

		try {

			buffer = new BufferedReader(
					new InputStreamReader(AppDirectory.getInputStream(CITIES_1000_FILE_NAME), Charset.forName("UTF-8")));

			while ((line = buffer.readLine()) != null) {

				if (line.startsWith("#")) {
					continue;
				}

				String[] values = line.split("\t");

				if (countryCode != null && !values[Cities1000Features.COUNTRY_CODE].equals(countryCode)) {
					continue;
				}

				CityEntity o = new CityEntity();

				o.setId(values[Cities1000Features.GEONAMEID]);
				o.setName((values[Cities1000Features.NAME]));
				o.setTerritoryCode(values[Cities1000Features.COUNTRY_CODE] + "." + values[Cities1000Features.ADMIN1]);
				o.setTimezone((values[Cities1000Features.TIMEZONE]));
				o.setLatitude((Double.parseDouble(values[Cities1000Features.LATITUDE])));
				o.setLongitude((Double.parseDouble(values[Cities1000Features.LONGITUDE])));

				cities.add(o);
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return cities;
	}

	private static ArrayList<LanguageEntity> loadLanguages() {

		Logger.get().info("Loading languages data from resource file");

		ArrayList<LanguageEntity> languages = new ArrayList<>();

		BufferedReader buffer;
		String line = null;

		try {

			buffer = new BufferedReader(
					new InputStreamReader(AppDirectory.getInputStream(LANGUAGE_CODES_FILE_NAME), Charset.forName("UTF-8")));

			while ((line = buffer.readLine()) != null) {

				if (line.startsWith("#")) {
					continue;
				}

				String[] values = line.split(",");

				LanguageEntity o = new LanguageEntity();
				o.setCode((values[0].replace("\"", "")));
				o.setLangName((values[1].replace("\"", "")));

				languages.add(o);
			}

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return languages;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static String getCurrencyCode(String countryCode) {
		return ((CountryEntity) ofy().load().type(CountryEntity.class).id(countryCode).safe()).getCurrencyCode();
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static String getCurrencyName(String countryCode) {
		return ((CountryEntity) ofy().load().type(CountryEntity.class).id(countryCode).safe()).getCurrencyName();
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static String getTerritoryName(String territoryCode) {
		return ((TerritoryEntity) ofy().load().type(TerritoryEntity.class).id(territoryCode).safe()).getTerritoryName();
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static String getCountryName(String countryCode) {
		return ((CountryEntity) ofy().load().type(CountryEntity.class).id(countryCode).safe()).getCountryName();
	}
	
	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static String getCountryDialingCode(String countryCode) {
		return ((CountryEntity) ofy().load().type(CountryEntity.class).id(countryCode).safe()).getDialingCode();
	}

	@Unexposed
	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getCountryNames(List<String> countryCodes) {
		Map<String, String> result = new HashMap<>(countryCodes.size());

		ofy().load().type(CountryEntity.class).ids(countryCodes).forEach((k, v) -> {
			result.put(k, v.getCountryName());
		});

		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static String getCityName(String cityId) {
		return ((CityEntity) ofy().load().type(CityEntity.class).id(cityId).safe()).getName();
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getCountryNames() {
		Map<String, String> result = new FluentHashMap<>();
		ofy().load().type(CountryEntity.class).forEach(e -> {
			result.put(e.getCode(), e.getCountryName());
		});
		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getCurrencyNames() {
		Map<String, String> result = new FluentHashMap<>();
		ofy().load().type(CountryEntity.class).forEach(e -> {
			result.put(e.getCurrencyCode(), e.getCurrencyName());
		});
		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getAvailableLocales(String countryCode) {
		Map<String, String> locales = new HashMap<>();
		for (Locale o : Locale.getAvailableLocales()) {
			if (o.getCountry().equals(countryCode)) {
				locales.put(o.toLanguageTag(), o.getDisplayName());
			}
		}
		return locales;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getAllLocales() {
		Map<String, String> locales = new HashMap<>();
		for (Locale o : Locale.getAvailableLocales()) {
			locales.put(o.toLanguageTag(), o.getDisplayName());
		}
		return locales;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getAllTimezones() {
		Map<String, String> timezones = new HashMap<>();
		for (String id : TimeZone.getAvailableIDs()) {
			timezones.put(id, id);
		}
		return timezones;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getLanguageNames(List<String> languageCodes) {
		Map<String, String> result = new FluentHashMap<>();
		ofy().load().type(LanguageEntity.class).ids(languageCodes).forEach((k, v) -> {
			result.put(k, v.getLangName());
		});
		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getTerritoryNames(String countryCode) {
		Map<String, String> result = new FluentHashMap<>();
		ofy().load().type(TerritoryEntity.class).filter("countryCode = ", countryCode).forEach(e -> {
			result.put(e.getCode(), e.getTerritoryName());
		});
		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getCityNames(String territoryCode) {
		Map<String, String> result = new FluentHashMap<>();
		ofy().load().type(CityEntity.class).filter("territoryCode = ", territoryCode).forEach(e -> {
			result.put(e.getId(), e.getName());
		});
		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Coordinates getCoordinates(int cityId) {
		CityEntity e = ofy().load().type(CityEntity.class).id(cityId).safe();
		return new Coordinates(e.getLatitude(), e.getLongitude());
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static Map<String, String> getSpokenLanguages(String countryCode) {

		// Languages to fetch
		ArrayList<String> languages = new ArrayList<>();

		CountryEntity e = ofy().load().type(CountryEntity.class).id(countryCode).safe();

		// First, add the country default language
		String languageCode = Locale.forLanguageTag(e.getLanguageCode()).getLanguage();
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

	@ModelMethod(functionality = RoleFunctionalities.GET_LOCATION_DATA)
	public static String getTimezone(int cityId) {
		return ((CityEntity) ofy().load().type(CityEntity.class).id(cityId).safe()).getTimezone();
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
