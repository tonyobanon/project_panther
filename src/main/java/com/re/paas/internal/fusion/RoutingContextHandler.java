package com.re.paas.internal.fusion;

import java.lang.reflect.Method;

import com.re.paas.api.annotations.AppClassLoaderInstrinsic;
import com.re.paas.api.fusion.HttpStatusCodes;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.fusion.services.ServiceDescriptor;

@AppClassLoaderInstrinsic
public class RoutingContextHandler {

	public static void handle(ServiceDescriptor sDescriptor, RoutingContext ctx) {

		// Verify Scheme
		if (sDescriptor.getEndpoint().requireSSL()) {
			if (!ctx.request().isSecure()) {
				ctx.response().setStatus(HttpStatusCodes.SC_NOT_ACCEPTABLE).flushBuffer();
			}
		}

		if (sDescriptor.getEndpoint().cache()) {
			// allow proxies to cache the data
			ctx.response().setHeader("Cache-Control", "public, max-age=" + ServiceDelegate.DEFAULT_CACHE_MAX_AGE);
		} else {
			ctx.response().setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
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
