package com.re.paas.internal.fusion.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Note;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.ThreadContext;
import com.re.paas.api.fusion.server.HttpServerRequest;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.Realm;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.core.keys.CacheKeys;
import com.re.paas.internal.models.ApplicationModel;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;

public class FusionHelper {

	public static Long getUserIdFromToken(String sessionToken) {

		if (sessionToken == null) {
			return null;
		}

		return (Long) CacheAdapter.get(CacheKeys.SESSION_TOKEN_TO_USER_ID_$TOKEN.replace("$TOKEN", sessionToken));
	}

	public static Long getUserId(HttpServerRequest req) {
		String userId = req.getParam(ServiceDelegate.USER_ID_PARAM_NAME);
		return userId != null ? Long.parseLong(userId) : null;
	}

	public static void setUserId(HttpServerRequest req, Long userId) {
		req.params().add(ServiceDelegate.USER_ID_PARAM_NAME, userId.toString());

	}

	public static List<String> getRoles(Long userId) {
		return new FluentArrayList<String>().with(BaseUserModel.getRole(userId));
	}

	public static final String sessionTokenName() {
		return "X-Session-Token";
	}

	/***
	 * 
	 * This returns an arbitrary functionality that the current user has access to.
	 * 
	 * @param ctx
	 * @return
	 */
	public static Functionality getCanonicalFrontend(RoutingContext ctx) {

		// Get sessionToken from either a cookie or request header
		String sessionToken;
		try {
			sessionToken = ctx.getCookie(FusionHelper.sessionTokenName()).getValue();
		} catch (NullPointerException e) {
			sessionToken = ctx.request().getHeader(FusionHelper.sessionTokenName());
		}

		Long userId = FusionHelper.getUserIdFromToken(sessionToken);

		String roleName = FusionHelper.getRoles(userId).get(0);
		Collection<Functionality> functionalities = Realm.getDelegate().getRoleFunctionalities(roleName);

		for (Functionality f : functionalities) {
			if (f.isFrontend()) {
				return f;
			}
		}

		return null;
	}

	private static Map<String, String> getRealmVariablesFromContext() {
		@SuppressWarnings("unchecked")
		Map<String, String> m = (Map<String, String>) ThreadContext.get(ThreadContext.REALM_VARIABLES);
		return m;
	}

	public static void addRealmVariablesToContext(Map<String, String> variables) {
		if (variables != null && !variables.isEmpty()) {
			ThreadContext.set(ThreadContext.REALM_VARIABLES, variables);
		}
	}

	/**
	 * This fetches realm variables from the user's session. These variables must
	 * have been added via a call to
	 * {@link FusionHelper#addRealmVariablesToSession(RoutingContext, Map)}
	 * 
	 * @param ctx
	 * @return
	 */
	public static Map<String, String> getRealmVariablesFromSession(RoutingContext ctx) {
		@SuppressWarnings("unchecked")
		Map<String, String> o = (Map<String, String>) ctx.session().get(SessionKeys.USER_VARIABLES_FIELD);
		return o;
	}

	/**
	 * This saves realm variables to the session.
	 * 
	 * @param ctx
	 * @param variables
	 */
	public static void addRealmVariablesToSession(RoutingContext ctx, Map<String, String> variables) {
		ctx.session().put(SessionKeys.USER_VARIABLES_FIELD, variables);
	}

	/**
	 * This reads the realm variables for the specified user from the database
	 * 
	 * @param userId
	 * @return
	 */
	public static Map<String, String> getRealmVariables(Long userId) {

		String role = BaseUserModel.getRole(userId);
		Realm realm = RoleModel.getRealm(role);

		List<String> variableNames = realm.applicationSpec().getVariableNames();

		if (!variableNames.isEmpty()) {

			Long applicationId = BaseUserModel.getApplicationId(userId);

			Map<String, String> variableValues = ApplicationModel.getFieldValues(applicationId, variableNames);

			return variableValues;

		} else {
			return Collections.emptyMap();
		}
	}

	@Note
	@BlockerTodo
	public static boolean isAccessAllowed(String roleName, Functionality functionality) {

		Collection<String> functionalities = Realm.getDelegate().getRoleFunctionalitiesAstring(roleName);

		// We need to transform functionalities to include variables

		@SuppressWarnings("unchecked")
		Map<String, String> actualVariables = getRealmVariablesFromContext();

		boolean b = functionalities.contains(Functionality.toString(functionality));

		return b;
	}

	public static void response(RoutingContext ctx, Object data) {
		ctx.response().write(Json.getGson().toJson(data));
	}

	public static void response(RoutingContext ctx, String data) {
		ctx.response().write(data);
	}

	/**
	 * For some reason, if the response is just a Number, this will cause the client
	 * side javascript promise to fail
	 */
	public static void response(RoutingContext ctx, Number data) {
		ctx.response().write(new JsonObject().put("data", data).encode());
	}
}
