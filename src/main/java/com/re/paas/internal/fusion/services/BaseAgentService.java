package com.re.paas.internal.fusion.services;

import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationMessageSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationReviewSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationWhistleblowMessageSpec;
import com.re.paas.apps.rex.classes.spec.AgentSpec;
import com.re.paas.apps.rex.classes.spec.IssueResolution;
import com.re.paas.apps.rex.functionality.AgentFunctionalities;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.internal.classes.GsonFactory;
import com.re.paas.internal.fusion.services.impl.FusionHelper;
import com.re.paas.internal.models.BaseAgentModel;


public class BaseAgentService extends BaseService {

	@Override
	public String uri() {
		return "/base-agents";
	}
	
	@FusionEndpoint(uri = "/list-agent-organization-names", requestParams = {
			"territory" }, method = HttpMethod.GET, 
					functionality = AgentOrganizationFunctionalities.Constants.LIST_AGENT_ORGANIZATION_NAMES)
	public void listAgentOrganizationNames(RoutingContext ctx) {

		String territory = ctx.request().getParam("territory");

		Map<Long, String> names = BaseAgentModel.listAgentOrganizationNames(territory);

		ctx.response().write(GsonFactory.getInstance().toJson(names));
	}

	@FusionEndpoint(uri = "/get-agent-organization", requestParams = {
			"id" }, method = HttpMethod.GET, 
					functionality =  AgentOrganizationFunctionalities.Constants.VIEW_AGENT_ORGANIZATION)
	public void getAgentOrganization(RoutingContext ctx) {

		Long id = Long.parseLong(ctx.request().getParam("id"));

		AgentOrganizationSpec spec = BaseAgentModel.getAgentOrganization(id);

		ctx.response().write(GsonFactory.getInstance().toJson(spec));
	}

	@FusionEndpoint(uri = "/get-agents", bodyParams = {
			"ids" }, method = HttpMethod.POST, 
					functionality = AgentFunctionalities.Constants.VIEW_AGENT)
	public void getAgents(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		List<Long> ids = GsonFactory.getInstance().fromJson(body.getJsonArray("ids").encode(),
				new TypeToken<List<Long>>() {
				}.getType());

		List<AgentSpec> spec = BaseAgentModel.getAgents(ids);

		ctx.response().write(GsonFactory.getInstance().toJson(spec));
	}

	@FusionEndpoint(uri = "/new-agent-organization-message", bodyParams = {
			"spec" }, method = HttpMethod.PUT, 
					functionality = AgentOrganizationFunctionalities.Constants.CREATE_AGENT_ORGANIZATION_MESSAGES)
	public void newAgentOrganizationMessage(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		AgentOrganizationMessageSpec spec = GsonFactory.getInstance().fromJson(body.getJsonObject("spec").encode(),
				AgentOrganizationMessageSpec.class);

		spec.setUserId(principal);

		BaseAgentModel.newAgentOrganizationMessage(spec);

		ctx.response().write(GsonFactory.getInstance().toJson(spec));
	}

	@FusionEndpoint(uri = "/update-agent-organization-message", requestParams = { "id",
			"resolution" }, method = HttpMethod.POST, 
			functionality = AgentOrganizationFunctionalities.Constants.UPDATE_AGENT_ORGANIZATION_MESSAGES)
	public void updateAgentOrganizationMessage(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));
		IssueResolution resolution = IssueResolution.from(Integer.parseInt(ctx.request().getParam("resolution")));

		BaseAgentModel.updateAgentOrganizationMessage(id, principal, resolution);
	}

	@FusionEndpoint(uri = "/delete-agent-organization-message", requestParams = {
			"id" }, method = HttpMethod.DELETE, 
					functionality = AgentOrganizationFunctionalities.Constants.DELETE_AGENT_ORGANIZATION_MESSAGES)
	public void deleteAgentOrganizationMessage(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		BaseAgentModel.deleteAgentOrganizationMessage(id, principal);
	}

	@FusionEndpoint(uri = "/get-agent-organization-message", requestParams = {
			"id" }, method = HttpMethod.GET, 
					functionality = AgentOrganizationFunctionalities.Constants.VIEW_AGENT_ORGANIZATION_MESSAGE)
	public void getAgentOrganizationMessage(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		AgentOrganizationMessageSpec spec = BaseAgentModel.getAgentOrganizationMessage(id, principal);

		ctx.response().write(GsonFactory.getInstance().toJson(spec));
	}

	@FusionEndpoint(uri = "/new-agent-organization-whistleblow-message", bodyParams = {
			"spec" }, method = HttpMethod.PUT, 
					functionality = AgentOrganizationFunctionalities.Constants.CREATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES)
	public void newAgentOrganizationWhistleblowMessage(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		AgentOrganizationWhistleblowMessageSpec spec = GsonFactory.getInstance()
				.fromJson(body.getJsonObject("spec").encode(), AgentOrganizationWhistleblowMessageSpec.class);

		spec.setUserId(principal);

		BaseAgentModel.newAgentOrganizationWhistleblowMessage(spec);

		ctx.response().write(GsonFactory.getInstance().toJson(spec));
	}

	@FusionEndpoint(uri = "/update-agent-organization-whistleblow-message", requestParams = { "id",
			"resolution" }, method = HttpMethod.POST, 
			functionality = AgentOrganizationFunctionalities.Constants.UPDATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES)
	public void updateAgentOrganizationWhistleblowMessage(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));
		IssueResolution resolution = IssueResolution.from(Integer.parseInt(ctx.request().getParam("resolution")));

		BaseAgentModel.updateAgentOrganizationWhistleblowMessage(id, principal, resolution);
	}

	@FusionEndpoint(uri = "/delete-agent-organization-whistleblow-message", requestParams = {
			"id" }, method = HttpMethod.DELETE, 
					functionality = AgentOrganizationFunctionalities.Constants.DELETE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES)
	public void deleteAgentOrganizationWhistleblowMessage(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		BaseAgentModel.deleteAgentOrganizationWhistleblowMessage(id, principal);
	}

	@FusionEndpoint(uri = "/get-agent-organization-whistleblow-message", requestParams = {
			"id" }, method = HttpMethod.GET, 
					functionality = AgentOrganizationFunctionalities.Constants.VIEW_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGE)
	public void getAgentOrganizationWhistleblowMessage(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		AgentOrganizationWhistleblowMessageSpec spec = BaseAgentModel.getAgentOrganizationWhistleblowMessage(id,
				principal);

		ctx.response().write(GsonFactory.getInstance().toJson(spec));
	}

	@FusionEndpoint(uri = "/new-agent-organization-review", bodyParams = {
			"spec" }, method = HttpMethod.PUT, 
					functionality = AgentOrganizationFunctionalities.Constants.CREATE_AGENT_ORGANIZATION_REVIEW)
	public void newAgentOrganizationReview(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		AgentOrganizationReviewSpec spec = GsonFactory.getInstance().fromJson(body.getJsonObject("spec").encode(),
				AgentOrganizationReviewSpec.class);

		spec.setUserId(principal);

		BaseAgentModel.newAgentOrganizationReview(spec);
		ctx.response().write(GsonFactory.getInstance().toJson(spec));
	}

	@FusionEndpoint(uri = "/delete-agent-organization-review", requestParams = {
			"id" }, method = HttpMethod.DELETE, 
					functionality = AgentOrganizationFunctionalities.Constants.DELETE_AGENT_ORGANIZATION_REVIEWS)
	public void deleteAgentOrganizationReview(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());

		Long id = Long.parseLong(ctx.request().getParam("id"));

		BaseAgentModel.deleteAgentOrganizationReview(id, principal);
	}
}
