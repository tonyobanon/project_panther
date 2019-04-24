package com.re.paas.internal.fusion.services;

import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.apps.rex.classes.spec.AdvancedPropertySearch;
import com.re.paas.apps.rex.classes.spec.CityFeaturesSpec;
import com.re.paas.apps.rex.classes.spec.ListedProperty;
import com.re.paas.apps.rex.classes.spec.ListedRentPropertySpec;
import com.re.paas.apps.rex.classes.spec.ListedSalePropertySpec;
import com.re.paas.apps.rex.classes.spec.PropertyAvailabilityStatus;
import com.re.paas.apps.rex.classes.spec.PropertyContractType;
import com.re.paas.apps.rex.classes.spec.PropertyFloorPlanSpec;
import com.re.paas.apps.rex.classes.spec.PropertyListingRequest;
import com.re.paas.apps.rex.classes.spec.PropertyPOISpec;
import com.re.paas.apps.rex.classes.spec.PropertyPriceRuleSpec;
import com.re.paas.apps.rex.classes.spec.PropertySearchByAgentOrganization;
import com.re.paas.apps.rex.classes.spec.PropertySearchByCity;
import com.re.paas.apps.rex.classes.spec.PropertySearchCriteria;
import com.re.paas.apps.rex.classes.spec.PropertySpec;
import com.re.paas.apps.rex.classes.spec.PropertyType;
import com.re.paas.apps.rex.classes.spec.PropertyUpdateSpec;
import com.re.paas.apps.rex.functionality.PropertyFunctionalities;
import com.re.paas.apps.rex.models.BasePropertyModel;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.classes.spec.ClientSignature;
import com.re.paas.internal.fusion.services.impl.FusionHelper;

public class BasePropertyService extends BaseService {
	@Override
	public String uri() {
		return "/base-properties";
	}

	@FusionEndpoint(uri = "/set-city-features", bodyParams = {
			"spec" }, method = HttpMethod.PUT, 
					functionality = PropertyFunctionalities.Constants.SET_CITY_FEATURES)
	public void setCityFeatutures(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		CityFeaturesSpec spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
				CityFeaturesSpec.class);

		BasePropertyModel.setCityFeatutures(spec);
	}

	@FusionEndpoint(uri = "/get-city-features", requestParams = {
			"id" }, method = HttpMethod.GET, 
					functionality = PropertyFunctionalities.Constants.GET_CITY_FEATURES)
	public void getCityFeatutures(RoutingContext ctx) {

		Integer id = Integer.parseInt(ctx.request().getParam("id"));
		CityFeaturesSpec spec = BasePropertyModel.getCityFeatutures(id);

		FusionHelper.response(ctx, spec);
	}

	@FusionEndpoint(uri = "/new-property-type-feature", bodyParams = { "type",
			"features" }, method = HttpMethod.PUT, 
			functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_TYPE_FEATURES)
	public void newPropertyTypeFeature(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		PropertyType type = PropertyType.from(body.getInteger("type"));
		List<String> features = Json.getGson().fromJson(body.getJsonArray("features").encode(),
				new TypeToken<List<String>>() {
				}.getType());

		BasePropertyModel.newPropertyTypeFeature(type, features);
	}

	@FusionEndpoint(uri = "/delete-property-type-feature", bodyParams = {
			"ids" }, method = HttpMethod.PUT, 
					functionality = PropertyFunctionalities.Constants.DELETE_PROPERTY_TYPE_FEATURES)
	public void deletePropertyTypeFeature(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		List<Long> ids = Json.getGson().fromJson(body.getJsonArray("ids").encode(),
				new TypeToken<List<Long>>() {
				}.getType());

		BasePropertyModel.deletePropertyTypeFeature(ids);
	}

	@FusionEndpoint(uri = "/list-property-type-features", requestParams = {
			"type" }, method = HttpMethod.GET, 
					functionality = PropertyFunctionalities.Constants.LIST_PROPERTY_TYPE_FEATURES)
	public void listPropertyTypeFeatures(RoutingContext ctx) {

		PropertyType type = PropertyType.from(Integer.parseInt(ctx.request().getParam("type")));
		Map<Long, ClientRBRef> result = BasePropertyModel.listPropertyTypeFeatures(type);

		FusionHelper.response(ctx, result);
	}

	@FusionEndpoint(uri = "/new-property-creation-request", bodyParams = {
			"spec" }, method = HttpMethod.PUT, 
					functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_CREATION_REQUEST)
	public void newPropertyCreationRequest(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();
		Long principal = FusionHelper.getUserId(ctx.request());

		PropertySpec spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(), PropertySpec.class);

		Long id = BasePropertyModel.newPropertyCreationRequest(principal, spec);
		FusionHelper.response(ctx, id);
	}

	@FusionEndpoint(uri = "/new-property-update-request", bodyParams = {
			"spec" }, method = HttpMethod.POST, 
					functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_UPDATE_REQUEST)
	public void newPropertyUpdateRequest(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();
		Long principal = FusionHelper.getUserId(ctx.request());

		PropertyUpdateSpec spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
				PropertyUpdateSpec.class);

		BasePropertyModel.newPropertyUpdateRequest(principal, spec);
	}

	@FusionEndpoint(uri = "/accept-property-request", requestParams = {
			"id" }, method = HttpMethod.POST, 
					functionality = PropertyFunctionalities.Constants.REVIEW_PROPERTY_REQUEST)
	public void acceptPropertyRequest(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		BasePropertyModel.acceptPropertyRequest(principal, id);
	}

	@FusionEndpoint(uri = "/decline-property-request", requestParams = {
			"id" }, method = HttpMethod.POST, 
					functionality = PropertyFunctionalities.Constants.REVIEW_PROPERTY_REQUEST)
	public void declinePropertyRequest(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		BasePropertyModel.declinePropertyRequest(principal, id);
	}

	@FusionEndpoint(uri = "/new-property-deletion-request", requestParams = {
			"id" }, method = HttpMethod.DELETE, 
					functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_DELETION_REQUEST)
	public void newPropertyDeletionRequest(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		BasePropertyModel.newPropertyDeletionRequest(principal, id);
	}

	@FusionEndpoint(uri = "/set-property-description", bodyParams = { "id",
			"description" }, method = HttpMethod.POST, 
			functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_UPDATE_REQUEST)
	public void setPropertyDescription(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();

		Long id = body.getLong("id");

		String description = body.getString("description");

		BasePropertyModel.setPropertyDescription(principal, id, description);
	}

	@FusionEndpoint(uri = "/get-property-description", requestParams = {
			"id" }, method = HttpMethod.GET, 
					functionality = PropertyFunctionalities.Constants.VIEW_PROPERTY)
	public void getPropertyDescription(RoutingContext ctx) {

		Long id = Long.parseLong(ctx.request().getParam("id"));

		String description = BasePropertyModel.getPropertyDescription(id);

		FusionHelper.response(ctx, description);
	}

	@FusionEndpoint(uri = "/set-property-poi", bodyParams = {
			"spec" }, method = HttpMethod.POST, 
					functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_UPDATE_REQUEST)
	public void setPropertyPOI(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();

		PropertyPOISpec spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
				PropertyPOISpec.class);

		BasePropertyModel.setPropertyPOI(principal, spec);
	}

	@FusionEndpoint(uri = "/get-property-poi", requestParams = {
			"id" }, method = HttpMethod.GET, 
					functionality = PropertyFunctionalities.Constants.VIEW_PROPERTY)
	public void getPropertyPOI(RoutingContext ctx) {

		Long id = Long.parseLong(ctx.request().getParam("id"));

		PropertyPOISpec spec = BasePropertyModel.getPropertyPOI(id);

		FusionHelper.response(ctx, spec);
	}

	@FusionEndpoint(uri = "/add-property-floor-plan", bodyParams = { "propertyId",
			"spec" }, method = HttpMethod.POST, 
			functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_UPDATE_REQUEST)
	public void addPropertyFloorPlan(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();

		Long propertyId = body.getLong("propertyId");

		PropertyFloorPlanSpec spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
				PropertyFloorPlanSpec.class);

		Long id = BasePropertyModel.addPropertyFloorPlan(principal, propertyId, spec);
		FusionHelper.response(ctx, id);
	}

	@FusionEndpoint(uri = "/remove-property-floor-plan", requestParams = { "propertyId",
			"id" }, method = HttpMethod.DELETE, 
			functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_UPDATE_REQUEST)
	public void removePropertyFloorPlan(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long propertyId = Long.parseLong(ctx.request().getParam("propertyId"));
		Long id = Long.parseLong(ctx.request().getParam("id"));

		BasePropertyModel.removePropertyFloorPlan(principal, propertyId, id);
	}

	@FusionEndpoint(uri = "/get-property-floor-plan", bodyParams = {
			"ids" }, method = HttpMethod.PUT, 
					functionality = PropertyFunctionalities.Constants.VIEW_PROPERTY)
	public void getPropertyFloorPlan(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		List<Long> ids = Json.getGson().fromJson(body.getJsonArray("ids").encode(),
				new TypeToken<List<Long>>() {
				}.getType());

		Map<Long, PropertyFloorPlanSpec> spec = BasePropertyModel.getPropertyFloorPlan(ids);
		FusionHelper.response(ctx, spec);
	}

	@FusionEndpoint(uri = "/add-to-user-saved-list", requestParams = { "userId",
			"propertyId" }, method = HttpMethod.PUT, 
			functionality = PropertyFunctionalities.Constants.ADD_TO_USER_SAVED_LIST)
	public void addToUserSavedList(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long userId = Long.parseLong(ctx.request().getParam("userId"));
		Long propertyId = Long.parseLong(ctx.request().getParam("propertyId"));

		BasePropertyModel.addToUserSavedList(principal, userId, propertyId);
	}

	@FusionEndpoint(uri = "/add-to-own-saved-list", requestParams = {
			"propertyId" }, method = HttpMethod.PUT, 
					functionality = PropertyFunctionalities.Constants.ADD_TO_OWN_SAVED_LIST)
	public void addToOwnSavedList(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long propertyId = Long.parseLong(ctx.request().getParam("propertyId"));

		BasePropertyModel.addToOwnSavedList(principal, propertyId);
	}

	@FusionEndpoint(uri = "/remove-from-user-saved-list", requestParams = { "userId",
			"propertyId" }, method = HttpMethod.DELETE, 
			functionality = PropertyFunctionalities.Constants.REMOVE_FROM_USER_SAVED_LIST)
	public void removeFromUserSavedList(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long userId = Long.parseLong(ctx.request().getParam("userId"));
		Long propertyId = Long.parseLong(ctx.request().getParam("propertyId"));

		BasePropertyModel.removeFromUserSavedList(principal, userId, propertyId);
	}

	@FusionEndpoint(uri = "/remove-from-own-saved-list", requestParams = {
			"propertyId" }, method = HttpMethod.DELETE, 
					functionality = PropertyFunctionalities.Constants.REMOVE_FROM_OWN_SAVED_LIST)
	public void removeFromOwnSavedList(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long propertyId = Long.parseLong(ctx.request().getParam("propertyId"));

		BasePropertyModel.removeFromOwnSavedList(principal, propertyId);
	}

	@FusionEndpoint(uri = "/get-user-saved-list", requestParams = {
			"userId" }, method = HttpMethod.GET, 
					functionality = PropertyFunctionalities.Constants.GET_USER_SAVED_LIST)
	public void getUserSavedList(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long userId = Long.parseLong(ctx.request().getParam("userId"));

		List<Long> ids = BasePropertyModel.getUserSavedList(principal, userId);
		FusionHelper.response(ctx, ids);
	}

	@FusionEndpoint(uri = "/get-own-saved-list", requestParams = {}, method = HttpMethod.GET, 
			functionality = PropertyFunctionalities.Constants.GET_OWN_SAVED_LIST)
	public void getOwnSavedList(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		List<Long> ids = BasePropertyModel.getOwnSavedList(principal);
		FusionHelper.response(ctx, ids);
	}

	@FusionEndpoint(uri = "/new-property-listing", bodyParams = { "contractType",
			"spec" }, method = HttpMethod.PUT, 
			functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_LISTING)
	public void newPropertyListing(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		PropertyContractType contractType = PropertyContractType.from(body.getInteger("contractType"));
		ListedProperty spec = null;

		switch (contractType) {
		case RENT:
			spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
					ListedRentPropertySpec.class);
			break;
		case SALE:
			spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
					ListedSalePropertySpec.class);
			break;
		}

		Long id = BasePropertyModel.newPropertyListing(principal, spec);

		FusionHelper.response(ctx, id);
	}

	@FusionEndpoint(uri = "/update-property-listing", bodyParams = { "contractType",
			"spec" }, method = HttpMethod.POST, 
			functionality = PropertyFunctionalities.Constants.UPDATE_PROPERTY_LISTING)
	public void updatePropertyListing(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		PropertyContractType contractType = PropertyContractType.from(body.getInteger("contractType"));
		ListedProperty spec = null;

		switch (contractType) {
		case RENT:
			spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
					ListedRentPropertySpec.class);
			break;
		case SALE:
			spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
					ListedSalePropertySpec.class);
			break;
		}

		BasePropertyModel.updatePropertyListing(principal, spec);
	}

	@FusionEndpoint(uri = "/delete-property-listing", requestParams = {
			"id" }, method = HttpMethod.DELETE, 
					functionality = PropertyFunctionalities.Constants.DELETE_PROPERTY_LISTING)
	public void deletePropertyListing(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		BasePropertyModel.deletePropertyListing(principal, id);
	}

	@FusionEndpoint(uri = "/get-property-listings", requestParams = {
			"propertyId" }, method = HttpMethod.GET, 
					functionality = PropertyFunctionalities.Constants.GET_PROPERTY_LISTINGS)
	public void getPropertyListings(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long propertyId = Long.parseLong(ctx.request().getParam("propertyId"));

		List<ListedProperty> result = BasePropertyModel.getPropertyListings(principal, propertyId);
		FusionHelper.response(ctx, result);
	}

	@FusionEndpoint(uri = "/update-property-listing-availability-status", requestParams = { "id",
			"status" }, method = HttpMethod.POST, 
			functionality = PropertyFunctionalities.Constants.UPDATE_PROPERTY_LISTING_AVAILABILITY_STATUS)
	public void updatePropertyListingAvailabilityStatus(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));
		PropertyAvailabilityStatus status = PropertyAvailabilityStatus
				.from(Integer.parseInt(ctx.request().getParam("status")));

		BasePropertyModel.updatePropertyListingAvailabilityStatus(principal, id, status);
	}

	@FusionEndpoint(uri = "/list-properties", bodyParams = { "criteria", "spec",
			"pageSize" }, method = HttpMethod.POST, 
			functionality = PropertyFunctionalities.Constants.LIST_PROPERTY)
	public void listProperties(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();

		// Todo: Generate Client Signature
		ClientSignature signature = new ClientSignature();

		PropertySearchCriteria criteria = PropertySearchCriteria.from(body.getInteger("criteria"));

		PropertyListingRequest spec = null;

		switch (criteria) {
		case ADVANCED_SEARCH:
			spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
					AdvancedPropertySearch.class);
			break;
		case BY_AGENT_ORGANIZATION:
			spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
					PropertySearchByAgentOrganization.class);
			break;
		case BY_CITY:
			spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(), PropertySearchByCity.class);
			break;
		}

		Integer pageSize = body.getInteger("pageSize");

		String context = BasePropertyModel.listProperties(principal, signature, spec, pageSize);

		FusionHelper.response(ctx, context);
	}

	@FusionEndpoint(uri = "/new-property-price-rule", bodyParams = {
			"spec" }, method = HttpMethod.PUT, 
					functionality = PropertyFunctionalities.Constants.CREATE_PROPERTY_PRICE_RULE)
	public void newPropertyPriceRule(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();

		PropertyPriceRuleSpec spec = Json.fromJson(body.getJsonObject("spec").encode(),
				PropertyPriceRuleSpec.class);

		Long id = BasePropertyModel.newPropertyPriceRule(principal, spec);
		FusionHelper.response(ctx, id);
	}

	@FusionEndpoint(uri = "/update-property-price-rule", bodyParams = { "id",
			"spec" }, method = HttpMethod.POST, 
			functionality = PropertyFunctionalities.Constants.UPDATE_PROPERTY_PRICE_RULE)
	public void updatePropertyPriceRule(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();

		Long id = body.getLong("id");

		PropertyPriceRuleSpec spec = Json.fromJson(body.getJsonObject("spec").encode(),
				PropertyPriceRuleSpec.class);

		BasePropertyModel.updatePropertyPriceRule(principal, id, spec);
	}

	@FusionEndpoint(uri = "/delete-property-price-rule", requestParams = {
			"id" }, method = HttpMethod.DELETE, 
					functionality = PropertyFunctionalities.Constants.DELETE_PROPERTY_PRICE_RULE)
	public void deletePropertyPriceRule(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		BasePropertyModel.deletePropertyPriceRule(principal, id);
	}

}
