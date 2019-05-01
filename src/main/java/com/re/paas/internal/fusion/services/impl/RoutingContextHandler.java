package com.re.paas.internal.fusion.services.impl;

import java.lang.reflect.Method;

import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.HttpStatusCodes;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.server.ServiceDescriptor;

public class RoutingContextHandler {

	public static void handle(ServiceDescriptor sDescriptor, RoutingContext ctx) {

		// Verify Scheme
		if (sDescriptor.getEndpoint().requireSSL()) {
			if (!ctx.request().isSSL()) {
				ctx.response().setStatusCode(HttpStatusCodes.SC_NOT_ACCEPTABLE).end();
			}
		}

		if (sDescriptor.getEndpoint().cache()) {
			// allow proxies to cache the data
			ctx.response().putHeader("Cache-Control", "public, max-age=" + ServiceDelegate.DEFAULT_CACHE_MAX_AGE);
		} else {
			ctx.response().putHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		}

		ClassLoader cl = RoutingContextHandler.class.getClassLoader();

		try {

			@SuppressWarnings("unchecked")
			Class<? extends BaseService> clazz = (Class<? extends BaseService>) cl
					.loadClass(sDescriptor.getClassName());

			Method method = clazz.getDeclaredMethod(sDescriptor.getMethodName(), RoutingContext.class);

			method.invoke(null, ctx);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
