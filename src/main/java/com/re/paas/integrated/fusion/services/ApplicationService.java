package com.re.paas.integrated.fusion.services;

import java.util.Map;

import com.re.paas.api.fusion.FusionEndpoint;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.models.classes.ApplicationDeclineReason;
import com.re.paas.api.utils.JsonParser;
import com.re.paas.integrated.fusion.functionalities.UserApplicationFunctionalities;
import com.re.paas.integrated.models.ApplicationModel;
import com.re.paas.integrated.models.BaseUserModel;
import com.re.paas.internal.fusion.FusionHelper;
import com.re.paas.internal.fusion.services.ServiceHelper;
import com.re.paas.internal.utils.ObjectUtils;

public class ApplicationService extends BaseService {

	@Override
	public String uri() {
		return "/user-applications";
	}

	@FusionEndpoint(uri = "/create-application", bodyParams = {
			"roleName" }, method = HttpMethod.PUT, functionality = UserApplicationFunctionalities.Constants.CREATE_APPLICATION)
	public static void createApplication(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String roleName = body.getString("roleName");

		Long applicationId = ApplicationModel.newApplication(roleName);

		// Add to activity stream

		ctx.response().write(applicationId.toString()).end();
	}

	@FusionEndpoint(uri = "/update-application", bodyParams = { "applicationId",
			"values" }, method = HttpMethod.POST, functionality = UserApplicationFunctionalities.Constants.UPDATE_APPLICATION)
	public static void updateApplication(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long applicationId = Long.parseLong(body.getString("applicationId"));
		Map<String, Object> values = body.getJsonObject("values").getMap();

		ApplicationModel.updateApplication(applicationId, ObjectUtils.toStringMap(values));
	}

	@FusionEndpoint(uri = "/submit-application", bodyParams = {
			"applicationId" }, method = HttpMethod.PUT, functionality = UserApplicationFunctionalities.Constants.SUBMIT_APPLICATION)
	public static void submitApplication(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long applicationId = Long.parseLong(body.getString("applicationId"));

		ApplicationModel.submitApplication(applicationId);
	}

	@FusionEndpoint(uri = "/get-application-role", requestParams = {
			"applicationId" }, functionality = UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM)
	public static void getApplicationRole(RoutingContext ctx) {

		Long applicationId = Long.parseLong(ctx.request().getParam("applicationId"));
		String roleName = ApplicationModel.getApplicationRole(applicationId);

		ctx.response().write(new JsonObject().put("role", roleName).encode()).end();
	}

	@FusionEndpoint(uri = "/get-pdf-questionnaire", requestParams = {
			"roleName" }, functionality = UserApplicationFunctionalities.Constants.DOWNLOAD_QUESTIONNAIRE)
	public static void getPDFQuestionnaire(RoutingContext ctx) {

		String roleName = ctx.request().getParam("roleName");
		String blobId = ApplicationModel.getPDFQuestionnaire(roleName);

		ctx.response().write(new JsonObject().put("blobId", blobId).encode()).end();
	}

	@FusionEndpoint(uri = "/get-field-values", requestParams = {
			"applicationId" }, functionality = UserApplicationFunctionalities.Constants.UPDATE_APPLICATION)
	public static void getApplicationFieldValues(RoutingContext ctx) {

		Long principal = ServiceHelper.getUserId(ctx.request());
		Long applicationId = Long.parseLong(ctx.request().getParam("applicationId"));

		String roleName = BaseUserModel.getRole(principal);
		
		
		
		// Verify that this is allowed, via applicationSpec.baseObjectType.getFunctionality
		FusionHelper.isAccessAllowed(roleName, applicantRealm.applicationSpec().getReviewFunctionality());
			
		
		Map<String, String> result = ApplicationModel.getFieldValues(applicationId);

		ctx.response().write(JsonParser.get().toJson(result)).end();
	}

	@FusionEndpoint(uri = "/get-consolidated-field-values", requestParams = {
			"applicationId" }, functionality = UserApplicationFunctionalities.Constants.UPDATE_APPLICATION)
	public static void getConsolidatedApplicationFieldValues(RoutingContext ctx) {

		Long applicationId = Long.parseLong(ctx.request().getParam("applicationId"));

		Map<String, String> result = ApplicationModel.getFieldValues(applicationId, true);

		ctx.response().write(JsonParser.get().toJson(result)).end();
	}

	@FusionEndpoint(uri = "/accept-agent-organization-application", bodyParams = {
			"applicationId" }, method = HttpMethod.POST, functionality = AgentOrganizationFunctionalities.Constants.REVIEW_ORGANIZATION_ADMIN_APPLICATION)
	public static void acceptAgentOrganizationApplication(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = ServiceHelper.getUserId(ctx.request());

		Long applicationId = Long.parseLong(body.getString("applicationId"));

		ApplicationModel.validateAgentOrganizationReview(applicationId, principal);

		Long id = ApplicationModel.acceptApplication(principal, applicationId);
		ctx.response().write(id.toString());
	}

	@FusionEndpoint(uri = "/accept-agent-application", bodyParams = {
			"applicationId" }, method = HttpMethod.POST, functionality = AgentFunctionalities.Constants.REVIEW_AGENT_APPLICATION)
	public static void acceptAgentApplication(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = ServiceHelper.getUserId(ctx.request());

		Long applicationId = Long.parseLong(body.getString("applicationId"));

		ApplicationModel.validateAgentReview(applicationId, principal);

		Long id = ApplicationModel.acceptApplication(principal, applicationId);
		ctx.response().write(id.toString());
	}

	@FusionEndpoint(uri = "/accept-admin-application", bodyParams = {
			"applicationId" }, method = HttpMethod.POST, functionality = UserApplicationFunctionalities.Constants.REVIEW_ADMIN_APPLICATION)
	public static void acceptAdminApplication(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = ServiceHelper.getUserId(ctx.request());

		Long applicationId = Long.parseLong(body.getString("applicationId"));

		ApplicationModel.validateAdminReview(applicationId, principal);

		Long id = ApplicationModel.acceptApplication(principal, applicationId);
		ctx.response().write(id.toString());
	}

	@FusionEndpoint(uri = "/get-decline-reasons", functionality = AgentOrganizationFunctionalities.Constants.REVIEW_ORGANIZATION_ADMIN_APPLICATION)
	public static void getApplicationDeclineReasons(RoutingContext ctx) {

		Map<Integer, Object> result = ApplicationModel.getApplicationDeclineReasons();
		
		ctx.response().write(JsonParser.get().toJson(result));
	}

	@FusionEndpoint(uri = "/decline-agent-organization-application", bodyParams = { "applicationId",
			"reason" }, method = HttpMethod.PUT, functionality = AgentOrganizationFunctionalities.Constants.REVIEW_ORGANIZATION_ADMIN_APPLICATION)
	public static void declineAgentOrganizationApplication(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long applicationId = Long.parseLong(body.getString("applicationId"));
		Long principal = ServiceHelper.getUserId(ctx.request());
		Integer reason = Integer.parseInt(body.getString("reason"));

		ApplicationModel.validateAgentOrganizationReview(applicationId, principal);

		ApplicationModel.declineApplication(applicationId, principal, ApplicationDeclineReason.from(reason));
	}

	@FusionEndpoint(uri = "/decline-application", bodyParams = { "applicationId",
			"reason" }, method = HttpMethod.PUT, functionality = UserApplicationFunctionalities.Constants.REVIEW_ADMIN_APPLICATION)
	public static void declineAdminApplication(RoutingContext ctx) {
		
		JsonObject body = ctx.getBodyAsJson();
		
		Long applicationId = Long.parseLong(body.getString("applicationId"));
		Long principal = ServiceHelper.getUserId(ctx.request());
		Integer reason = Integer.parseInt(body.getString("reason"));
		
		ApplicationModel.declineApplication(applicationId, principal, ApplicationDeclineReason.from(reason));
	}

}
