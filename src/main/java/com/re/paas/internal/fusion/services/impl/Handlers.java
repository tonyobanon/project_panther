package com.re.paas.internal.fusion.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.classes.ThreadContext;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.Cookie;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.HttpStatusCodes;
import com.re.paas.api.fusion.server.Route;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.server.ServiceAuthenticator;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.internal.fusion.services.ResourceBundleService;
import com.re.paas.internal.models.LocaleModel;
import com.re.paas.internal.utils.LocaleUtils;

public class Handlers {

	private static Map<String, ServiceAuthenticator> customAuthenticators = new HashMap<>();

	static void APIAuthHandler(RoutingContext ctx) {

		String path = ctx.request().path().replace(AbstractServiceDelegate.BASE_PATH, "");

		ServiceAuthenticator authenticator = customAuthenticators.get(path);

		if (authenticator != null) {

			if (!authenticator.authenticate(ctx)) {
				ctx.response().setStatusCode(HttpStatusCodes.SC_UNAUTHORIZED)
						.write(ResponseUtil.toResponse(HttpStatusCodes.SC_UNAUTHORIZED)).end();
			}
			return;
		}

		// set json response type
		ctx.response().putHeader("Content-Type", "application/json");

		// do not allow proxies to cache the data
		ctx.response().putHeader("Cache-Control", "no-store, no-cache");

		String uri = ctx.request().path().replace(ServiceDelegate.BASE_PATH, "");
		HttpMethod method = ctx.request().method();

		Route route = new Route(uri, method);

		Functionality functionality = BaseService.getDelegate().getServiceFunctionality(route);

		if (functionality == null || !functionality.requiresBasicAuth()) {
			return;
		}

		boolean hasAccess = false;

		// Get sessionToken from either a cookie or request header
		String sessionToken;
		try {
			sessionToken = ctx.getCookie(FusionHelper.sessionTokenName()).getValue();
		} catch (NullPointerException e) {
			sessionToken = ctx.request().getHeader(FusionHelper.sessionTokenName());
		}

		Long userId = FusionHelper.getUserIdFromToken(sessionToken);

		if (userId != null) {
			hasAccess = true;
		}

		if (functionality.requiresAuth() && hasAccess) {
			hasAccess = false;
			for (String roleName : FusionHelper.getRoles(userId)) {

				// Check that this role has the right to access this Uri
				if (FusionHelper.isAccessAllowed(roleName, functionality)) {
					hasAccess = true;
					break;
				}
			}
		}

		if (hasAccess) {
			FusionHelper.setUserId(ctx.request(), userId);
		} else {
			ctx.response().setStatusCode(HttpStatusCodes.SC_UNAUTHORIZED)
					.write(ResponseUtil.toResponse(HttpStatusCodes.SC_UNAUTHORIZED)).end();
		}
	}

	static void addCustomAuthenticator(String uri, ServiceAuthenticator customAuthenticator) {
		Handlers.customAuthenticators.put(uri, customAuthenticator);
	}

	static Consumer<RoutingContext> defaultTailHandler() {
		return (ctx -> {
			// Remove thread local data
			ThreadContext.clear();
		});
	}

	static Consumer<RoutingContext> defaultHeadHandler() {
		return (ctx -> {

			// Note: container pools request threads, we need to create new LocalThread
			// context
			ThreadContext.newRequestContext();

			// Auth Handler
			Handlers.APIAuthHandler(ctx);

			// Detect User Locale
			Cookie localeCookie = ctx.getCookie(ResourceBundleService.DEFAULT_LOCALE_COOKIE);
			List<String> locales = new ArrayList<>();

			if (localeCookie != null) {
				locales.add(localeCookie.getValue());
			} else {
				ctx.acceptableLocales().forEach(locale -> {
					locales.add(locale.getLanguage() + LocaleUtils.LANGUAGE_COUNTRY_DELIMETER + locale.getCountry());
				});
			}
			LocaleModel.setUserLocale(locales, FusionHelper.getUserId(ctx.request()));

			// Realm variables must have been added on login
			// Add these variables from session to thread local context
			FusionHelper.addRealmVariablesToContext(FusionHelper.getRealmVariablesFromSession(ctx));

		});
	}

}
