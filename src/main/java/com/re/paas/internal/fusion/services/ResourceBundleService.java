package com.re.paas.internal.fusion.services;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.fusion.functionalities.LocationFunctionalities;
import com.re.paas.internal.models.LocationModel;
import com.re.paas.internal.models.RBModel;
import com.re.paas.internal.utils.LocaleUtils;


public class ResourceBundleService extends BaseService {
	
	@Override
	public String uri() {
		return "/resource-bundle";
	}

	public static final String DEFAULT_LOCALE_COOKIE = "DEFAULT_LOCALE";

	@FusionEndpoint(uri = "/get-available-countries", 
			functionality = LocationFunctionalities.Constants.GET_AVAILABLE_COUNTRIES)
	public static void getAvailableCountries(RoutingContext ctx) {

		// K: locale, V: name, code
		Map<String, JsonObject> result = new HashMap<>();

		RBModel.getLocaleCountries().forEach((language, countries) -> {

			LocationModel.getCountryNames(countries).forEach((code, name) -> {

				String k = language + LocaleUtils.LANGUAGE_COUNTRY_DELIMETER + code;

				JsonObject v = new JsonObject();
				v.addProperty("code", code);
				v.addProperty("name", name);

				result.put(k, v);
			});

		});

		ctx.response().write(Json.getGson().toJson(result));
	}

	@FusionEndpoint(uri = "/get-rb-entry", bodyParams = {
			"keys" }, method = HttpMethod.POST, 
					functionality = LocationFunctionalities.Constants.GET_RESOURCE_BUNDLE_ENTRIES)
	public static void getRbEntry(RoutingContext ctx) {

		Map<String, Object> keys = ctx.getBodyAsJson().getJsonObject("keys").getMap();
		Map<String, String> result = new HashMap<>();

		keys.forEach((k, v) -> {
			result.put(k, RBModel.get(v.toString()));
		});

		ctx.response().write(Json.getGson().toJson(result));
	}

}
