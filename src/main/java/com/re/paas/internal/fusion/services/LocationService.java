package com.re.paas.internal.fusion.services;

import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.core.keys.CacheKeys;
import com.re.paas.internal.fusion.functionalities.LocationFunctionalities;
import com.re.paas.internal.models.LocationModel;


public class LocationService extends BaseService {
@Override
public String uri() {
	return "/locations/service";
}
	@FusionEndpoint(uri = "/countryList",
			functionality = LocationFunctionalities.Constants.GET_COUNTRY_NAMES)
	public static void getCountries(RoutingContext ctx) {
		String json = (String) CacheAdapter.get(CacheKeys.COUNTRY_NAMES);
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = Json.getGson().toJson(LocationModel.getCountryNames());
			CacheAdapter.put(CacheKeys.COUNTRY_NAMES, json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/currencyList",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getCurrencies(RoutingContext ctx) {
		String json = (String) CacheAdapter.get(CacheKeys.CURRENCY_NAMES);
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = Json.getGson().toJson(LocationModel.getCurrencyNames());
			CacheAdapter.put(CacheKeys.CURRENCY_NAMES, json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/localeList", requestParams = "countryCode",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getLocales(RoutingContext ctx) {

		String countryCode = ctx.request().getParam("countryCode");

		if (countryCode != null && !countryCode.equals("null")) {
			String json = (String) CacheAdapter.get(CacheKeys.LOCALES_$COUNTRY.replace("$COUNTRY", countryCode));
			if (json != null) {
				ctx.response().write(json);
			} else {
				json = Json.getGson().toJson(LocationModel.getAvailableLocales(countryCode));
				CacheAdapter.put(CacheKeys.LOCALES_$COUNTRY.replace("$COUNTRY", countryCode), json);
				ctx.response().write(json);
			}
		} else {
			String json = (String) CacheAdapter.get(CacheKeys.LOCALES);
			if (json != null) {
				ctx.response().write(json);
			} else {
				json = Json.getGson().toJson(LocationModel.getAllLocales());
				CacheAdapter.put(CacheKeys.LOCALES, json);
				ctx.response().write(json);
			}
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/timezoneList",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getTimezones(RoutingContext ctx) {
		String json = (String) CacheAdapter.get(CacheKeys.TIMEZONES);
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = Json.getGson().toJson(LocationModel.getAllTimezones());
			CacheAdapter.put(CacheKeys.TIMEZONES, json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/territoryList", requestParams = "ctx",
			functionality = LocationFunctionalities.Constants.GET_TERRITORY_NAMES)
	public static void getTerritories(RoutingContext ctx) {
		String countryCode = ctx.request().getParam("ctx");
		String json = (String) CacheAdapter.get(CacheKeys.TERRITORIES_$COUNTRY.replace("$COUNTRY", countryCode));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = Json.getGson().toJson(LocationModel.getTerritoryNames(countryCode));
			CacheAdapter.put(CacheKeys.TERRITORIES_$COUNTRY.replace("$COUNTRY", countryCode), json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/cityList", requestParams = "ctx",
			functionality = LocationFunctionalities.Constants.GET_CITY_NAMES)
	public static void getCities(RoutingContext ctx) {
		String territoryCode = ctx.request().getParam("ctx");
		String json = (String) CacheAdapter.get(CacheKeys.CITIES_$TERRITORY.replace("$TERRITORY", territoryCode));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = Json.getGson().toJson(LocationModel.getCityNames(territoryCode));
			CacheAdapter.put(CacheKeys.CITIES_$TERRITORY.replace("$TERRITORY", territoryCode), json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/cityCoordinates", requestParams = "cityCode",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getCityCoordinates(RoutingContext ctx) {
		String cityCode = ctx.request().getParam("cityCode");
		String json = (String) CacheAdapter.get(CacheKeys.COORDINATES_$CITY.replace("$CITY", cityCode));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = Json.getGson().toJson(LocationModel.getCoordinates(Integer.parseInt(cityCode)));
			CacheAdapter.put(CacheKeys.COORDINATES_$CITY.replace("$CITY", cityCode), json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/cityTimezone", requestParams = "cityCode",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getCityTimezone(RoutingContext ctx) {
		String cityCode = ctx.request().getParam("cityCode");
		String json = (String) CacheAdapter.get(CacheKeys.TIMEZONE_$CITY.replace("$CITY", cityCode));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = Json.getGson().toJson(LocationModel.getTimezone(Integer.parseInt(cityCode)));
			CacheAdapter.put(CacheKeys.TIMEZONE_$CITY.replace("$CITY", cityCode), json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/countryLanguages", requestParams = "countryCode",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getSpokenLanguages(RoutingContext ctx) {
		String countryCode = ctx.request().getParam("countryCode");
		String json = (String) CacheAdapter.get(CacheKeys.SPOKEN_LANGUAGES_$COUNTRY.replace("$COUNTRY", countryCode));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = Json.getGson().toJson(LocationModel.getSpokenLanguages(countryCode));
			CacheAdapter.put(CacheKeys.SPOKEN_LANGUAGES_$COUNTRY.replace("$COUNTRY", countryCode), json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

	@FusionEndpoint(uri = "/currencyCode", requestParams = "countryCode",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getCurrencyCode(RoutingContext ctx) {
		String countryCode = ctx.request().getParam("countryCode");
		String json = (String) CacheAdapter.get(CacheKeys.CURRENCY_CODE_$COUNTRY.replace("$COUNTRY", countryCode));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = "[\"" + LocationModel.getCurrencyCode(countryCode) + "\"]";
			
			CacheAdapter.put(CacheKeys.CURRENCY_CODE_$COUNTRY.replace("$COUNTRY", countryCode), json);
			ctx.response().write(json);
		} 
		ctx.response().end(); 
	}    
 
	@FusionEndpoint(uri = "/currencyName", requestParams = "countryCode",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getCurrencyName(RoutingContext ctx) {
		String countryCode = ctx.request().getParam("countryCode");
		String json = (String) CacheAdapter.get(CacheKeys.CURRENCY_NAME_$COUNTRY.replace("$COUNTRY", countryCode));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = "[\"" + LocationModel.getCurrencyName(countryCode) + "\"]";
			CacheAdapter.put(CacheKeys.CURRENCY_NAME_$COUNTRY.replace("$COUNTRY", countryCode), json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}
	
	@FusionEndpoint(uri = "/country-dialing-code", requestParams = "countryCode",
			functionality = LocationFunctionalities.Constants.GET_LOCATION_DATA)
	public static void getCountryDialingCode(RoutingContext ctx) {
		String countryCode = ctx.request().getParam("countryCode");
		String json = (String) CacheAdapter.get(CacheKeys.DIALING_CODE_$COUNTRY.replace("$COUNTRY", countryCode));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = "[\"" + LocationModel.getCountryDialingCode(countryCode) + "\"]";
			CacheAdapter.put(CacheKeys.DIALING_CODE_$COUNTRY.replace("$COUNTRY", countryCode), json);
			ctx.response().write(json);
		}
		ctx.response().end();
	}

}
