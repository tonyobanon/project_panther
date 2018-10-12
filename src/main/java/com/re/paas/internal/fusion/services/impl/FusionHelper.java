package com.re.paas.internal.fusion.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.annotations.Note;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.ThreadContext;
import com.re.paas.api.fusion.server.HttpServerRequest;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.Realm;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.classes.GsonFactory;
import com.re.paas.internal.core.keys.CacheKeys;
import com.re.paas.internal.models.BaseUserModel;

public class FusionHelper {

	public static Long getUserIdFromToken(String sessionToken) {

		if (sessionToken == null) {
			return null;
		}

		return (Long) CacheAdapter.get(CacheKeys.SESSION_TOKEN_TO_USER_ID_$TOKEN.replace("$TOKEN", sessionToken));
	}

	public static Long getUserId(HttpServerRequest req) {
		String userId = req.getParam(FusionServiceDelegate.USER_ID_PARAM_NAME);
		return userId != null ? Long.parseLong(userId) : null;
	}

	public static void setUserId(HttpServerRequest req, Long userId) {
		req.params().add(FusionServiceDelegate.USER_ID_PARAM_NAME, userId.toString());

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

	@Note
	@BlockerTodo
	public static boolean isAccessAllowed(String roleName, Functionality functionality) {		
		
		Collection<String> functionalities = Realm.getDelegate().getRoleFunctionalitiesAstring(roleName);
		
		// We need to transform functionalities to include variables
		
		@SuppressWarnings("unchecked")
		Map<String, String> actualVariables = (Map<String, String>) ThreadContext.get(Functionality.VARIABLES_FIELD);
		
		
		boolean b = functionalities.contains(Functionality.toString(functionality));
		
		return b;
	}

	public static void response(RoutingContext ctx, Object data) {
		ctx.response().write(GsonFactory.getInstance().toJson(data));
	}

	public static void response(RoutingContext ctx, String data) {
		ctx.response().write(data);
	}

	/**
	 * For some reason, if the response is just a Number, this will cause the client side javascript
	 * promise to fail
	 */
	public static void response(RoutingContext ctx, Number data) {
		ctx.response().write(new JsonObject().put("data", data).encode());
	}
}
