package com.re.paas.internal.fusion.services;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.reflect.TypeToken;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.listable.IndexedNameType;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.internal.classes.CursorMoveType;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.fusion.functionalities.SearchFunctionalities;
import com.re.paas.internal.fusion.services.impl.FusionHelper;
import com.re.paas.internal.models.SearchModel;
import com.re.paas.internal.utils.BackendObjectMarshaller;

public class SearchService extends BaseService {

	@Override
	public String uri() {
		return "/search/service";
	}

	@FusionEndpoint(uri = "/new-list-context", bodyParams = { "type", "pageSize", "listingFilters",
			"order" }, method = HttpMethod.PUT, 
			functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static void newListContext(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long userId = FusionHelper.getUserId(ctx.request());

		IndexedNameType type = IndexedNameType.fromString(body.getString("type"));
		Integer pageSize = body.getInteger("pageSize");
		String order = body.getString("order");

		List<ListingFilter> listingFilters = Json.getGson()
				.fromJson(body.getJsonArray("listingFilters").encode(), new TypeToken<List<ListingFilter>>() {
				}.getType());

		for (ListingFilter listingFilter : listingFilters) {

			for (Entry<String, Object> filter : listingFilter.getFilters().entrySet()) {
				// If there are any dates in <filters>, attempt parsing it correctly
				try {
					Date date = BackendObjectMarshaller.unmarshalDate(filter.getValue().toString());
					filter.setValue(date);
				} catch (ParseException | NumberFormatException e) {
				}
			}
		}

		String contextKey = SearchModel.newListContext(userId, type, pageSize,
				(order != null && !order.equals("undefined")) ? order : null, listingFilters);

		ctx.response().write(new JsonObject().put("contextKey", contextKey).encode());
	}

	@FusionEndpoint(uri = "/new-search-context", bodyParams = { "type", "phrase",
			"pageSize" }, method = HttpMethod.PUT, 
			functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static void newSearchContext(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long userId = FusionHelper.getUserId(ctx.request());

		IndexedNameType type = IndexedNameType.fromString(body.getString("type"));
		String phrase = body.getString("phrase");
		Integer pageSize = body.getInteger("pageSize");

		String contextKey = SearchModel.newSearchContext(userId, type, phrase, pageSize);

		ctx.response().write(new JsonObject().put("contextKey", contextKey).encode());
	}

	@FusionEndpoint(uri = "/clear-cache", requestParams = {
			"type" }, method = HttpMethod.DELETE, 
					functionality = PlatformFunctionalities.Constants.MANAGE_SYSTEM_CACHES)
	public static void clearSearchCache(RoutingContext ctx) {
		ListingType type = ListingType.from(Integer.parseInt(ctx.request().getParam("type")));
		SearchModel.clearCache(type);
	}

	@FusionEndpoint(uri = "/has-cursor", requestParams = { "moveType",
			"contextKey" }, method = HttpMethod.GET, 
			functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static void hasListingCursor(RoutingContext ctx) {

		Long userId = FusionHelper.getUserId(ctx.request());

		CursorMoveType moveType = CursorMoveType.from(Integer.parseInt(ctx.request().getParam("moveType")));
		String contextKey = ctx.request().getParam("contextKey");

		Boolean b = SearchModel.has(userId, moveType, contextKey);
		ctx.response().write(b.toString());
	}

	@FusionEndpoint(uri = "/is-context-available", requestParams = {
			"contextKey" }, method = HttpMethod.GET, 
					functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static void isListingContextAvailable(RoutingContext ctx) {

		String contextKey = ctx.request().getParam("contextKey");

		Boolean b = SearchModel.isContextAvailable(contextKey);
		ctx.response().write(b.toString());
	}

	@FusionEndpoint(uri = "/next-results", requestParams = { "moveType",
			"contextKey" }, method = HttpMethod.GET, 
			functionality = SearchFunctionalities.Constants.PERFORM_LIST_OPERATION)
	public static void nextListingResults(RoutingContext ctx) {

		Long userId = FusionHelper.getUserId(ctx.request());

		CursorMoveType moveType = CursorMoveType.from(Integer.parseInt(ctx.request().getParam("moveType")));
		String contextKey = ctx.request().getParam("contextKey");

		Map<?, ?> result = SearchModel.next(userId, moveType, contextKey);

		ctx.response().write(Json.getGson().toJson(result));
	}

	@FusionEndpoint(uri = "/get-searchable-lists", method = HttpMethod.GET, 
			functionality = SearchFunctionalities.Constants.GET_SEARCHABLE_LISTS)
	public static void getSearchableList(RoutingContext ctx) {

		Long userId = FusionHelper.getUserId(ctx.request());

		Map<String, SearchableUISpec> result = SearchModel.getSearchableLists(userId);
		ctx.response().write(Json.getGson().toJson(result));
	}

}
