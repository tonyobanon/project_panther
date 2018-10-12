package com.re.paas.internal.fusion.services;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.Cookie;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.fusion.ui.AbstractUIComponentDelegate;
import com.re.paas.api.models.classes.UserProfileSpec;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.classes.GsonFactory;
import com.re.paas.internal.classes.spec.BaseUserSpec;
import com.re.paas.internal.classes.spec.LoginIdType;
import com.re.paas.internal.core.keys.CacheKeys;
import com.re.paas.internal.core.keys.CacheValues;
import com.re.paas.internal.fusion.functionalities.UserFunctionalities;
import com.re.paas.internal.fusion.services.impl.FusionHelper;
import com.re.paas.internal.models.ApplicationModel;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.LocationModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.models.UserModel;


@BlockerTodo("Always verity user's email")
public class UserService extends BaseService {

	@Override
	public String uri() {
		return "/users";
	}
	

	@FusionEndpoint(uri = "/authenticateUser", headerParams = { "email", "pass", "rem" }, requestParams = { "idType",
			"returnUrl" }, functionality = UserFunctionalities.Constants.AUTHENTICATE)
	public void authenticateUser(RoutingContext ctx) {

		LoginIdType type = LoginIdType.from(Integer.parseInt(ctx.request().getParam("idType")));

		String pass = ctx.request().getHeader("pass");
		String rem = ctx.request().getHeader("rem");

		String returnUrl = ctx.request().getParam("returnUrl");

		try {

			Long userId = null;

			switch (type) {
			case EMAIL:
				String email = ctx.request().getHeader("email");
				userId = BaseUserModel.loginByEmail(email, pass);
				break;
			case PHONE:
				Long phone = Long.parseLong(ctx.request().getHeader("phone"));
				userId = BaseUserModel.loginByPhone(phone, pass);
				break;
			}

			String sessionToken = loginUser(userId, ctx.request().host(),
					rem.equals("true") ? CacheValues.SESSION_TOKEN_COOKIE_LONG_EXPIRY_IN_SECS
							: CacheValues.SESSION_TOKEN_COOKIE_SHORT_EXPIRY_IN_SECS);

			if (returnUrl.equals("null")) {
				returnUrl = AbstractUIComponentDelegate.DEFAULT_CONSOLE_URI;
			}

			
			// Add session token cookie
			
			Cookie cookie = new Cookie(FusionHelper.sessionTokenName(), sessionToken).setPath("/");

			cookie.setMaxAge(rem.equals("true") ? CacheValues.SESSION_TOKEN_COOKIE_LONG_EXPIRY_IN_SECS
					: CacheValues.SESSION_TOKEN_COOKIE_SHORT_EXPIRY_IN_SECS);

			ctx.addCookie(cookie);
			
			
			// Add functionality variables
			
			String role = BaseUserModel.getRole(userId);
			Realm realm = RoleModel.getRealm(role);
			
			List<String> variableNames = realm.applicationSpec().getVariableNames();
			
			if(!variableNames.isEmpty()) {

				Long applicationId = BaseUserModel.getApplicationId(userId);
				
				Map<String, String> variableValues = ApplicationModel.getFieldValues(applicationId, variableNames);
				ctx.session().put(Functionality.VARIABLES_FIELD, GsonFactory.getInstance().toJson(variableValues));
			}
						
			
			// Perform Http Redirect

			ctx.response().setStatusCode(HttpServletResponse.SC_FOUND);
			ctx.response().putHeader(getLocationHeader(), returnUrl);

		} catch (PlatformException e) {
			ctx.response().setStatusCode(HttpServletResponse.SC_UNAUTHORIZED);
		}
		ctx.response().end();
	}

	@BlockerBlockerTodo("Implement method to generate JwtTokens")
	private static String loginUser(Long userId, String remoteAdress, int expiry) {
		
		String sessionToken = Utils.newRandom();

		// Call JwtTokens to store Jwt Token		
		
		return sessionToken;
	}

	@FusionEndpoint(uri = "/get-own-profile", 
			functionality = UserFunctionalities.Constants.VIEW_OWN_PROFILE)
	public void getOwnProfile(RoutingContext ctx) {

		Long userId = FusionHelper.getUserId(ctx.request());

		String json = (String) CacheAdapter.get(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
		if (json != null) {
			ctx.response().write(json);
		} else {
			json = GsonFactory.getInstance().toJson(BaseUserModel.getProfile(userId));
			CacheAdapter.put(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()), json);
			ctx.response().write(json);
		}
	}

	@FusionEndpoint(uri = "/get-own-avatar", 
			functionality = UserFunctionalities.Constants.VIEW_OWN_PROFILE)
	public void getOwnAvatar(RoutingContext ctx) {
		Long userId = FusionHelper.getUserId(ctx.request());
		String image = BaseUserModel.getAvatar(userId);
		ctx.response().write(new JsonObject().put("image", image).encode());
	}

	@FusionEndpoint(uri = "/get-user-avatar", requestParams = {
			"userId" }, 
			functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public void geUserAvatar(RoutingContext ctx) {
		Long userId = Long.parseLong(ctx.request().getParam("userId"));
		String image = BaseUserModel.getAvatar(userId);
		ctx.response().write(new JsonObject().put("image", image).encode());
	}

	@FusionEndpoint(uri = "/get-own-role", 
			functionality = UserFunctionalities.Constants.VIEW_OWN_PROFILE)
	public void getOwnRole(RoutingContext ctx) {
		Long userId = FusionHelper.getUserId(ctx.request());
		String role = BaseUserModel.getRole(userId);
		ctx.response().write(new JsonObject().put("role", role).encode());
	}

	@FusionEndpoint(uri = "/get-user-role", requestParams = {
			"userId" }, 
			functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public void geUserRole(RoutingContext ctx) {
		Long userId = Long.parseLong(ctx.request().getParam("userId"));
		String role = BaseUserModel.getRole(userId);
		ctx.response().write(new JsonObject().put("role", role).encode());
	}

	@FusionEndpoint(uri = "/update-own-email", bodyParams = {
			"email" }, method = HttpMethod.POST, 
					functionality = UserFunctionalities.Constants.MANAGE_OWN_PROFILE)
	public void updateOwnEmail(RoutingContext ctx) {

		Long userId = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();
		String email = body.getString("email");

		BaseUserModel.updateEmail(null, userId, email);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/update-user-email", bodyParams = { "userId",
			"email" }, method = HttpMethod.POST, 
			functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public void updateUserEmail(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		Long userId = body.getLong("userId");
		String email = body.getString("email");

		BaseUserModel.updateEmail(principal, userId, email);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/update-own-phone", bodyParams = {
			"phone" }, method = HttpMethod.POST, 
					functionality = UserFunctionalities.Constants.MANAGE_OWN_PROFILE)
	public void updateOwnPhone(RoutingContext ctx) {

		Long userId = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();
		Long phone = Long.parseLong(body.getString("phone"));

		BaseUserModel.updatePhone(null, userId, phone);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/update-user-phone", bodyParams = { "userId",
			"phone" }, method = HttpMethod.POST, 
			functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public void updateUserPhone(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		Long userId = body.getLong("userId");
		Long phone = Long.parseLong(body.getString("phone"));

		BaseUserModel.updatePhone(principal, userId, phone);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/update-own-password", bodyParams = { "current",
			"newPassword" }, method = HttpMethod.POST, 
			functionality = UserFunctionalities.Constants.MANAGE_OWN_PROFILE)
	public void updateOwnPassword(RoutingContext ctx) {

		Long userId = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();
		String current = body.getString("current");
		String newPassword = body.getString("newPassword");

		BaseUserModel.updatePassword(null, userId, current, newPassword);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/update-user-password", bodyParams = { "userId", "current",
			"newPassword" }, method = HttpMethod.POST, 
			functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public void updateUserPassword(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		Long userId = body.getLong("userId");
		String current = body.getString("current");
		String newPassword = body.getString("newPassword");

		BaseUserModel.updatePassword(principal, userId, current, newPassword);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/update-own-avatar", bodyParams = {
			"blobId" }, method = HttpMethod.POST, 
					functionality = UserFunctionalities.Constants.MANAGE_OWN_PROFILE)
	public void updateOwnAvatar(RoutingContext ctx) {

		Long userId = FusionHelper.getUserId(ctx.request());

		JsonObject body = ctx.getBodyAsJson();
		String blobId = body.getString("blobId");

		BaseUserModel.updateAvatar(null, userId, blobId);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/update-user-avatar", bodyParams = { "userId",
			"blobId" }, method = HttpMethod.POST, 
			functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public void updateUserAvatar(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		Long userId = body.getLong("userId");
		String blobId = body.getString("blobId");

		BaseUserModel.updateAvatar(principal, userId, blobId);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/update-role", bodyParams = { "userId",
			"role" }, method = HttpMethod.POST, 
			functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public void updateUserRole(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		Long principal = FusionHelper.getUserId(ctx.request());

		Long userId = Long.parseLong(body.getString("userId"));
		String role = body.getString("role");

		BaseUserModel.updateRole(principal, userId, role);
		CacheAdapter.del(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
	}

	@FusionEndpoint(uri = "/get-person-name", requestParams = { "userId",
			"full" }, 
			functionality = UserFunctionalities.Constants.GET_PERSON_NAMES)
	public void getPersonName(RoutingContext ctx) {

		Long userId = null;

		if (ctx.request().getParam("userId").equals("undefined")) {
			userId = FusionHelper.getUserId(ctx.request());
		} else {
			userId = Long.parseLong(ctx.request().getParam("userId"));
		}

		Boolean full = Boolean.parseBoolean(ctx.request().getParam("full"));

		Object personName = BaseUserModel.getPersonName(userId, full);

		ctx.response().write(new JsonObject().put("name", personName).encode());
	}

	@FusionEndpoint(uri = "/get-user-profile", requestParams = {
			"userId" }, 
			functionality = UserFunctionalities.Constants.GET_USER_PROFILE)
	public void getUserProfile(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());
		Long userId = Long.parseLong(ctx.request().getParam("userId"));

		String json = (String) CacheAdapter.get(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()));
		if (json != null) {
			ctx.response().write(json);
		} else {

			UserProfileSpec spec = BaseUserModel.getProfile(principal, userId);
			spec.setCityName(LocationModel.getCityName(spec.getCity().toString()))
					.setTerritoryName(LocationModel.getTerritoryName(spec.getTerritory()))
					.setCountryName(LocationModel.getCountryName(spec.getCountry()))
					.setCountryDialingCode(LocationModel.getCountryDialingCode(spec.getCountry()));

			json = GsonFactory.getInstance().toJson(spec);
			CacheAdapter.put(CacheKeys.USER_PROFILE_$USER.replace("$USER", userId.toString()), json);
			ctx.response().write(json);
		}
	}

	@FusionEndpoint(uri = "/get-suggested-profiles", requestParams = {
			"userId" }, 
			functionality = UserFunctionalities.Constants.GET_USER_PROFILE)
	public void getSuggestedProfiles(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());
		Long userId = Long.parseLong(ctx.request().getParam("userId"));

		List<BaseUserSpec> profiles = UserModel.getSuggestedProfiles(principal, userId);

		ctx.response().write(GsonFactory.getInstance().toJson(profiles));
	}

	@FusionEndpoint(uri = "/can-access-user-profile", requestParams = {
			"userId" }, 
			functionality = UserFunctionalities.Constants.GET_USER_PROFILE)
	public void canAccessUserProfile(RoutingContext ctx) {

		Long principal = FusionHelper.getUserId(ctx.request());
		Long userId = Long.parseLong(ctx.request().getParam("userId"));

		boolean b = BaseUserModel.canAccessUserProfile(principal, userId);

		ctx.response().write(GsonFactory.getInstance().toJson(b));
	}

}
