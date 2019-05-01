package com.re.paas.internal.fusion.services;

import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.realms.Realm;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.fusion.functionalities.FormFunctionalities;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.fusion.functionalities.UserApplicationFunctionalities;
import com.re.paas.internal.models.FormModel;
import com.re.paas.internal.models.helpers.FormFieldRepository;
import com.re.paas.internal.models.helpers.FormFieldRepository.FormField;

public class FormService extends BaseService {

	@Override
	public String uri() {
		return "/forms";
	}

	@FusionEndpoint(uri = "/new-application-form-section", bodyParams = { "name", "description",
			"realm" }, method = HttpMethod.PUT, functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static void newApplicationFormSection(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String name = body.getString("name");
		String description = body.getString("description");

		Realm roleRealm = Realm.get(body.getString("realm"));

		FormModel.newSection(name, description, FormSectionType.APPLICATION_FORM, roleRealm);
	}

	@FusionEndpoint(uri = "/new-system-configuration-section", bodyParams = { "name", "description",
			"realm" }, method = HttpMethod.PUT, functionality = PlatformFunctionalities.Constants.MANAGE_SYSTEM_CONFIGURATION_FORM)
	public static void newSystemConfigurationSection(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String name = body.getString("name");
		String description = body.getString("description");

		Realm roleRealm = Realm.get(body.getString("realm"));

		FormModel.newSection(name, description, FormSectionType.SYSTEM_CONFIGURATION, roleRealm);
	}

	@FusionEndpoint(uri = "/list-application-form-sections", requestParams = {
			"realm" }, functionality = FormFunctionalities.Constants.VIEW_APPLICATION_FORM)
	public static void listApplicationFormSections(RoutingContext ctx) {

		Realm roleRealm = Realm.get(ctx.request().getParam("realm"));
		
		List<Section> sections = FormModel.listSections(FormSectionType.APPLICATION_FORM, roleRealm);
		ctx.response().write(Json.getGson().toJson(sections)).end();
	}

	@FusionEndpoint(uri = "/list-system-configuration-sections", functionality = PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION)
	public static void listSystemConfigurationSections(RoutingContext ctx) {

		List<Section> sections = FormModel.listSections(FormSectionType.SYSTEM_CONFIGURATION, null);
		ctx.response().write(Json.getGson().toJson(sections)).end();
	}

	@FusionEndpoint(uri = "/list-application-form-fields", requestParams = {
			"sectionId" }, functionality = FormFunctionalities.Constants.VIEW_APPLICATION_FORM)
	public static void listApplicationFormFields(RoutingContext ctx) {

		String sectionId = ctx.request().getParam("sectionId");

		List<AbstractField> fields = FormModel.getFields(FormSectionType.APPLICATION_FORM, sectionId);
		ctx.response().write(Json.getGson().toJson(fields)).end();
	}

	@FusionEndpoint(uri = "/list-application-form-field-names", requestParams = {
			"sectionId" }, functionality = FormFunctionalities.Constants.VIEW_APPLICATION_FORM)
	public static void listApplicationFormFieldNames(RoutingContext ctx) {

		String sectionId = ctx.request().getParam("sectionId");

		Map<String, ClientRBRef> fields = FormModel.getFieldNames(FormSectionType.APPLICATION_FORM, sectionId);
		ctx.response().write(Json.getGson().toJson(fields)).end();
	}

	@FusionEndpoint(uri = "/list-all-application-form-fields", bodyParams = {
			"sectionIds" }, method = HttpMethod.POST, functionality = FormFunctionalities.Constants.VIEW_APPLICATION_FORM)
	public static void listAllApplicationFormFields(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		List<String> sectionIds = Json.getGson().fromJson(body.getJsonArray("sectionIds").encode(),
				new TypeToken<List<String>>() {
				}.getType());

		Map<String, List<AbstractField>> fields = FormModel.getAllFields(FormSectionType.APPLICATION_FORM, sectionIds);
		ctx.response().write(Json.getGson().toJson(fields)).end();
	}

	@FusionEndpoint(uri = "/list-system-configuration-fields", requestParams = {
			"sectionId" }, functionality = PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION)
	public static void listSystemConfigurationFields(RoutingContext ctx) {

		String sectionId = ctx.request().getParam("sectionId");

		List<AbstractField> fields = FormModel.getFields(FormSectionType.SYSTEM_CONFIGURATION, sectionId);
		ctx.response().write(Json.getGson().toJson(fields)).end();
	}

	@FusionEndpoint(uri = "/list-system-configuration-field-names", requestParams = {
			"sectionId" }, functionality = PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION)
	public static void listSystemConfigurationFieldNames(RoutingContext ctx) {

		String sectionId = ctx.request().getParam("sectionId");

		Map<String, ClientRBRef> fields = FormModel.getFieldNames(FormSectionType.SYSTEM_CONFIGURATION, sectionId);
		ctx.response().write(Json.getGson().toJson(fields)).end();
	}

	@FusionEndpoint(uri = "/list-all-system-configuration-fields", bodyParams = {
			"sectionIds" }, method = HttpMethod.POST, functionality = PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION)
	public static void listAllSystemConfigurationFields(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		List<String> sectionIds = Json.getGson().fromJson(body.getJsonArray("sectionIds").encode(),
				new TypeToken<List<String>>() {
				}.getType());

		Map<String, List<AbstractField>> fields = FormModel.getAllFields(FormSectionType.SYSTEM_CONFIGURATION, sectionIds);
		ctx.response().write(Json.getGson().toJson(fields)).end();
	}

	@FusionEndpoint(uri = "/delete-application-form-section", requestParams = {
			"sectionId" }, method = HttpMethod.DELETE, functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static void deleteApplicationFormSection(RoutingContext ctx) {

		String sectionId = ctx.request().getParam("sectionId");
		FormModel.deleteSection(sectionId, FormSectionType.APPLICATION_FORM);
	}

	@FusionEndpoint(uri = "/delete-system-configuration-section", requestParams = {
			"sectionId" }, method = HttpMethod.DELETE, functionality = PlatformFunctionalities.Constants.MANAGE_SYSTEM_CONFIGURATION_FORM)
	public static void deleteSystemConfigurationSection(RoutingContext ctx) {

		String sectionId = ctx.request().getParam("sectionId");
		FormModel.deleteSection(sectionId, FormSectionType.SYSTEM_CONFIGURATION);
	}

	@FusionEndpoint(uri = "/delete-application-form-field", requestParams = {
			"fieldId" }, method = HttpMethod.DELETE, functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static void deleteApplicationFormField(RoutingContext ctx) {

		String fieldId = ctx.request().getParam("fieldId");
		FormModel.deleteField(FormSectionType.APPLICATION_FORM, fieldId);
	}

	@FusionEndpoint(uri = "/delete-system-configuration-field", requestParams = {
			"fieldId" }, method = HttpMethod.DELETE, functionality = PlatformFunctionalities.Constants.MANAGE_SYSTEM_CONFIGURATION_FORM)
	public static void deleteSystemConfigurationField(RoutingContext ctx) {

		String fieldId = ctx.request().getParam("fieldId");
		FormModel.deleteField(FormSectionType.SYSTEM_CONFIGURATION, fieldId);
	}

	@FusionEndpoint(uri = "/create-application-form-simple-field", bodyParams = { "sectionId",
			"spec" }, method = HttpMethod.PUT, functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static void newApplicationFormSimpleField(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String sectionId = body.getString("sectionId");
		SimpleField spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(), SimpleField.class);

		String id = FormModel.newSimpleField(FormSectionType.APPLICATION_FORM, sectionId, spec);

		ctx.response().write(id).end();
	}

	@FusionEndpoint(uri = "/create-application-form-composite-field", bodyParams = { "sectionId",
			"spec" }, method = HttpMethod.PUT, functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static void newApplicationFormCompositeField(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String sectionId = body.getString("sectionId");
		CompositeField spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
				CompositeField.class);

		String id = FormModel.newCompositeField(FormSectionType.APPLICATION_FORM, sectionId, spec);

		ctx.response().write(id).end();
	}

	@FusionEndpoint(uri = "/create-system-configuration-simple-field", bodyParams = { "sectionId",
			"spec" }, method = HttpMethod.PUT, functionality = PlatformFunctionalities.Constants.MANAGE_SYSTEM_CONFIGURATION_FORM)
	public static void newSystemConfigurationSimpleField(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String sectionId = body.getString("sectionId");
		SimpleField spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(), SimpleField.class);

		String id = FormModel.newSimpleField(FormSectionType.SYSTEM_CONFIGURATION, sectionId, spec);

		ctx.response().write(id).end();
	}

	@FusionEndpoint(uri = "/create-system-configuration-composite-field", bodyParams = { "sectionId",
			"spec" }, method = HttpMethod.PUT, functionality = PlatformFunctionalities.Constants.MANAGE_SYSTEM_CONFIGURATION_FORM)
	public static void newSystemConfigurationCompositeField(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String sectionId = body.getString("sectionId");
		CompositeField spec = Json.getGson().fromJson(body.getJsonObject("spec").encode(),
				CompositeField.class);

		String id = FormModel.newCompositeField(FormSectionType.SYSTEM_CONFIGURATION, sectionId, spec);

		ctx.response().write(id).end();
	}

	@FusionEndpoint(uri = "/get-application-form-field-ids", requestParams = {
			"realm" }, functionality = FormFunctionalities.Constants.GET_FORM_FIELD_IDS)
	public static void getApplicationFormFieldIds(RoutingContext ctx) {
		
		Realm roleRealm = Realm.get(ctx.request().getParam("realm"));

		Map<FormField, String> result = FormFieldRepository.getFieldIds(roleRealm);

		ctx.response().write(Json.getGson().toJson(result)).end();
	}
}
