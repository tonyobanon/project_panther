package com.re.paas.integrated.fusion.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.re.paas.api.fusion.FusionEndpoint;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.realms.Realm;
import com.re.paas.integrated.fusion.functionalities.RoleFunctionalities;
import com.re.paas.integrated.models.BaseUserModel;
import com.re.paas.integrated.models.RoleModel;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.core.keys.CacheKeys;
import com.re.paas.internal.fusion.services.ServiceHelper;

public class RolesService extends BaseService {

	@Override
	public String uri() {
		return "/roles";
	}

	@FusionEndpoint(uri = "/new-role", bodyParams = { "roleName",
			"realm" }, method = HttpMethod.PUT, functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void newRole(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String roleName = body.getString("roleName");
		Realm roleRealm = Realm.get(body.getString("realm"));

		RoleModel.newRole(roleName, roleRealm);
	}

	@FusionEndpoint(uri = "/role", requestParams = {
			"roleName" }, method = HttpMethod.DELETE, functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void deleteRole(RoutingContext ctx) {
		String roleName = ctx.request().getParam("roleName");
		RoleModel.deleteRole(roleName);
	}

	@FusionEndpoint(uri = "/list", requestParams = {
			"realm" }, method = HttpMethod.GET, functionality = RoleFunctionalities.Constants.LIST_ROLES)
	public static void listRoles(RoutingContext ctx) {

		Realm realm = Realm.get(ctx.request().getParam("realm"));

		Map<String, String> roles = realm.equals("undefined") ? RoleModel.listRoles() : RoleModel.listRoles(realm);

		ctx.response().write(Json.getGson().toJson(roles));
	}

	@FusionEndpoint(uri = "/user-count", bodyParams = {
			"roleNames" }, method = HttpMethod.POST, functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void getUsersCount(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();
		List<String> names = Json.getGson().fromJson(body.getJsonArray("roleNames").encode(),
				new TypeToken<List<String>>() {
				}.getType());

		Map<String, Integer> result = RoleModel.getUsersCount(names);
		ctx.response().write(Json.getGson().toJson(result));
	}

	@FusionEndpoint(uri = "/does-user-role-allow", bodyParams = {
			"functionalities" }, method = HttpMethod.POST, functionality = RoleFunctionalities.Constants.GET_ROLE_FUNCTIONALITIES)
	public static void doesUserRoleAllow(RoutingContext ctx) {

		Long principal = ServiceHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();

		List<Integer> functionalities_ = Json.getGson()
				.fromJson(body.getJsonArray("functionalities").encode(), new TypeToken<List<Integer>>() {
				}.getType());

		List<RoleFunctionalities> functionalities = new ArrayList<>();

		functionalities_.forEach(i -> {
			functionalities.add(RoleFunctionalities.from(i));
		});

		String roleName = BaseUserModel.getRole(principal);

		Boolean isAllowed = RoleModel.isAccessAllowed(roleName,
				functionalities.toArray(new RoleFunctionalities[functionalities.size()]));
		ctx.response().write(Json.getGson().toJson(isAllowed));
	}

	@FusionEndpoint(uri = "/realms", method = HttpMethod.GET, functionality = RoleFunctionalities.Constants.GET_ROLE_REALMS)
	public static void listRealms(RoutingContext ctx) {
		Map<String, String> roles = RoleModel.listRoles();
		ctx.response().write(Json.getGson().toJson(roles));
	}

	/**
	 * This retrieves all the functionalities applicable to this role realm
	 */
	@FusionEndpoint(uri = "/realm-functionalities", requestParams = {
			"realm" }, functionality = RoleFunctionalities.Constants.GET_REALM_FUNCTIONALITIES)
	public static void getRealmFunctionalities(RoutingContext ctx) {
		String roleName = ctx.request().getParam("role");
		Realm realm = RoleModel.getRealm(roleName);
		String json = Json.getGson().toJson(RoleModel.getRealmFunctionalities(realm));
		ctx.response().write(json);
	}

	/**
	 * This retrieves all the functionalities for this role
	 */
	@FusionEndpoint(uri = "/functionalities", requestParams = {
			"roleName" }, functionality = RoleFunctionalities.Constants.GET_ROLE_FUNCTIONALITIES)
	public static void getRoleFunctionalities(RoutingContext ctx) {

		String roleName = ctx.request().getParam("roleName");
		List<String> e = RoleModel.getRoleFunctionalities(roleName);

		CacheAdapter.putString(CacheKeys.ROLE_FUNCTIONALITIES_$ROLE.replace("$ROLE", roleName), new JsonArray(e).toString());

		String json = Json.getGson().toJson(e);
		ctx.response().write(json);
	}

	@FusionEndpoint(uri = "/update-spec", bodyParams = { "roleName", "functionality",
			"action" }, method = HttpMethod.POST, functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void updateRoleSpec(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		String roleName = body.getString("roleName");
		String functionalityString = body.getString("functionality");
		Boolean add = body.getBoolean("add");

		RoleModel.updateRoleSpec(roleName, add, Functionality.fromString(functionalityString));

		CacheAdapter.del(CacheKeys.ROLE_FUNCTIONALITIES_$ROLE.replace("$ROLE", roleName));
	}

	@FusionEndpoint(uri = "/default-role", requestParams = {
			"realm" }, functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void getDefaultRole(RoutingContext ctx) {
		Realm roleRealm = Realm.get(ctx.request().getParam("realm"));
		String role = RoleModel.getDefaultRole(roleRealm);
		ctx.response().write(role);
	}

	@FusionEndpoint(uri = "/get-role-realm", requestParams = {
			"role" }, functionality = RoleFunctionalities.Constants.GET_ROLE_REALMS)
	public static void getRoleRealm(RoutingContext ctx) {
		String roleName = ctx.request().getParam("role");
		Realm realm = RoleModel.getRealm(roleName);
		ctx.response().write(Json.getGson().toJson(realm));
	}

}
