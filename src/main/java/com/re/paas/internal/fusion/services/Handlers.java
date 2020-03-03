package com.re.paas.internal.fusion.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.classes.ThreadContext;
import com.re.paas.api.fusion.Cookie;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.HttpStatusCodes;
import com.re.paas.api.fusion.Route;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.fusion.services.ServiceAuthenticator;
import com.re.paas.integrated.fusion.services.ResourceBundleService;
import com.re.paas.integrated.models.LocaleModel;
import com.re.paas.internal.fusion.FusionHelper;
import com.re.paas.internal.utils.LocaleUtils;

public class Handlers {

	private static Map<String, ServiceAuthenticator> customAuthenticators = new HashMap<>();

	private static void authHandler(RoutingContext ctx) {

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

		String uri = ctx.request().path().replace(AbstractServiceDelegate.BASE_PATH, "");
		HttpMethod method = ctx.request().method();

		Route route = new Route(uri, method);

		Functionality functionality = BaseService.getDelegate().getFunctionality(route);

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
			ServiceHelper.setUserId(ctx.request(), userId);
		} else {
			ctx.response().setStatusCode(HttpStatusCodes.SC_UNAUTHORIZED)
					.write(ResponseUtil.toResponse(HttpStatusCodes.SC_UNAUTHORIZED)).end();
		}
	}

	static void addCustomAuthenticator(String uri, ServiceAuthenticator customAuthenticator) {
		customAuthenticators.put(uri, customAuthenticator);
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
			authHandler(ctx);

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
			LocaleModel.setUserLocale(locales, ServiceHelper.getUserId(ctx.request()));

			// Realm variables must have been added on login
			// Add these variables from session to thread local context
			
			FusionHelper.addRealmVariablesToContext(FusionHelper.getRealmVariablesFromSession(ctx));

		});
	}

}
