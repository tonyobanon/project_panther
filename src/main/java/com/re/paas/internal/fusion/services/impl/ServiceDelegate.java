package com.re.paas.internal.fusion.services.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.DefaultServiceAuthenticator;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.HttpStatusCodes;
import com.re.paas.api.fusion.server.Route;
import com.re.paas.api.fusion.server.RouteHandler;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Platform;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.fusion.ui.impl.RPCFactory;
import com.re.paas.internal.runtime.spi.AppProvisioner;

@BlockerTodo("Create optionalRequestParams setting, Validate request params in fusion. Do in main ctx handler. Add support for service docs")
/**
 * Note: This class calls LocaleModel, to set user locale
 */

@DelegateSpec(dependencies = { SpiType.CLOUD_ENVIRONMENT, SpiType.FUNCTIONALITY })
public class ServiceDelegate extends AbstractServiceDelegate {

	private static final Integer DEFAULT_CACHE_MAX_AGE = 259200;

	private static final String FUSION_CLIENT_PATH = System.getProperty("java.io.tmpdir") + File.separator
			+ Platform.getPlatformPrefix() + File.separator + "fusion-service-clients" + File.separator;

	public static Pattern endpointClassUriPattern = Pattern
			.compile("\\A\\Q/\\E[a-zA-Z]+[-]*[a-zA-Z]+(\\Q/\\E[a-zA-Z]+[-]*[a-zA-Z]+)*\\z");

	public static Pattern endpointMethodUriPattern = Pattern.compile("\\A\\Q/\\E[a-zA-Z-]+[-]*[a-zA-Z]+\\z");

	private static final String ROUTE_HANDLER_RK_PREFIX = "rhrkp_";
	private static final String ROUTE_HANDLER_KEYS = "rhk";

	private static final String ROUTE_FUNCTIONALITY_RK_PREFIX = "rfrkp_";
	private static final String FUNCTIONALITY_ROUTES_RK_PREFIX = "frrkp_";

	protected static final String USER_ID_PARAM_NAME = "x_uid";
	public static final String BASE_PATH = "/api";

	@Override
	public Multimap<Route, RouteHandler> getRouteHandlers() {

		List<String> routes = getList(String.class, ROUTE_HANDLER_KEYS);

		Multimap<Route, RouteHandler> result = LinkedHashMultimap.create(routes.size(), 2);

		routes.forEach(r -> {
			Route route = Route.fromString(r);
			getList(RouteHandler.class, ROUTE_HANDLER_RK_PREFIX + route).forEach(h -> {
				result.put(route, h);
			});
		});

		return result;
	}

	@Override
	public List<RouteHandler> getRouteHandlers(Route route) {
		String namespace = (ROUTE_HANDLER_RK_PREFIX + route.toString());
		return getList(RouteHandler.class, namespace);
	}

	private void addRouteHandler(Route route, RouteHandler handler) {
		String namespace = ROUTE_HANDLER_RK_PREFIX + route.toString();
		addToList(namespace, handler);
		addToList(ROUTE_HANDLER_KEYS, route.toString());
	}

	@Override
	public Functionality getRouteFunctionality(Route route) {
		String namespace = (ROUTE_FUNCTIONALITY_RK_PREFIX + route.toString());
		return Functionality.fromString(get(namespace).toString());
	}

	private void setRouteFunctionality(String route, Functionality functionality) {
		String namespace = ROUTE_FUNCTIONALITY_RK_PREFIX + route;
		if (!hasKey(namespace)) {
			set(namespace, Functionality.toString(functionality));
		} else {
			Exceptions.throwRuntime("Route: " + route + " already exists");
		}
	}

	@Override
	public List<String> getFunctionalityRoute(Functionality functionality) {
		String namespace = (FUNCTIONALITY_ROUTES_RK_PREFIX + Functionality.toString(functionality));
		return getList(String.class, namespace);
	}

	private void addFunctionalityRoute(Functionality functionality, String route) {
		String namespace = FUNCTIONALITY_ROUTES_RK_PREFIX + Functionality.toString(functionality);
		addToList(namespace, route);
	}

	@Override
	public DelegateInitResult init() {

		CacheAdapter.start();

		Logger.get().debug("Scanning for API routes");

		// Sample code to add a handler to a particular route
		//addRouteHandler(new Route(), new RouteHandler((ctx) -> {} , false));

		// Then, add fusion services found in classpath

		ObjectWrapper<StringBuilder> serviceCientBuffer = new ObjectWrapper<StringBuilder>().set(new StringBuilder());

		// This is used to avoid duplicate service methods, since they all exists in a
		// global client context
		Map<String, String> methodNames = new HashMap<>();

		scanAll(context -> {
			
			Class<?> serviceClass = context.getService().getClass();

			String className = serviceClass.getSimpleName();
			String methodName = context.getMethod().getName();

			if (methodNames.containsKey(methodName)) {

				String msg = "Method name: " + methodName + "(..) in " + className + " already exists in "
						+ methodNames.get(methodName);
				throw new ResourceException(ResourceException.RESOURCE_ALREADY_EXISTS, msg);
			}

			methodNames.put(methodName, className);

			Functionality functionality = Functionality.fromString(context.getEndpoint().functionality());

			// Get appId that owns this service
			String appId = serviceClass.getClassLoader() instanceof AppClassLoader ? ((AppClassLoader)serviceClass.getClassLoader()).getAppId() : AppProvisioner.DEFAULT_APP_ID;
			
			String uri = "/" + appId + context.getService().uri() + context.getEndpoint().uri();
			HttpMethod httpMethod = context.getEndpoint().method();

			Route route = new Route(uri, HttpMethod.valueOf(httpMethod.name()));

			Logger.get().trace("Mapping route: " + uri + " (" + httpMethod + ") to functionality: " + functionality);

			setRouteFunctionality(route.toString(), functionality);

			addFunctionalityRoute(functionality, uri);

			if (context.getEndpoint().createXhrClient()) {
				// Generate XHR clients
				serviceCientBuffer.get().append(RPCFactory.generateXHRClient(context.getService(), context.getMethod(),
						context.getEndpoint(), route));
			}

			if (context.getEndpoint().customAuthenticator() != DefaultServiceAuthenticator.class) {
				// Register custom authenticator
				Handlers.addCustomAuthenticator(uri,
						ClassUtils.createInstance(context.getEndpoint().customAuthenticator()));
			}

			// Add Handler

			RouteHandler handler = new RouteHandler(((ctx) -> {

				// Verify Scheme
				if (context.getEndpoint().requireSSL()) {
					if (!ctx.request().isSSL()) {
						ctx.response().setStatusCode(HttpStatusCodes.SC_NOT_ACCEPTABLE).end();
					}
				}

				if (context.getEndpoint().cache()) {
					// allow proxies to cache the data
					ctx.response().putHeader("Cache-Control", "public, max-age=" + DEFAULT_CACHE_MAX_AGE);
				} else {
					ctx.response().putHeader("Cache-Control", "no-cache, no-store, must-revalidate");
				}

				try {
					context.getMethod().invoke(context.getService(), ctx);
				} catch (Exception e) {
					ctx.response().setStatusCode(HttpStatusCodes.SC_INTERNAL_SERVER_ERROR).end(
							com.re.paas.internal.fusion.services.impl.ResponseUtil.toResponse(ErrorHelper.getError(e)));
				}

			}), context.getEndpoint().isBlocking());

			addRouteHandler(route, handler);

			// Generate Javascript client

			if (context.isClassEnd() && !CloudEnvironment.get().isProduction()) {
				saveServiceClient(serviceCientBuffer.get().toString(), context.getService());
				serviceCientBuffer.set(new StringBuilder());
			}

		});
		return DelegateInitResult.SUCCESS;
	}

	/**
	 * This discovers fusion services by scanning the classpath
	 */
	private void scanAll(Consumer<FusionServiceContext> consumer) {

		Logger.get().debug("Scanning for services");

		forEach(c -> {
			
			final BaseService service = ClassUtils.createInstance(c);

			if (!endpointClassUriPattern.matcher(service.uri()).matches()) {
				throw new RuntimeException("Improper URI format for " + c.getName());
			}

			List<Method> methodsList = new ArrayList<Method>();

			for (Method m : c.getDeclaredMethods()) {

				// Note: Lambda functions are compiled as synthetic members of the declaring
				// class, and some private helper methods may be contained in Service classes

				if (!m.isSynthetic() /* Skip Lambdas */ && m.getAnnotation(FusionEndpoint.class) != null) {
					methodsList.add(m);
				}
			}

			Method[] methods = methodsList.toArray(new Method[methodsList.size()]);

			for (int i = 0; i < methods.length; i++) {

				Method method = methods[i];

				if (!method.isAnnotationPresent(FusionEndpoint.class)) {
					// Silently ignore
					continue;
				}

				FusionEndpoint endpoint = method.getAnnotation(FusionEndpoint.class);

				if (!endpointMethodUriPattern.matcher(endpoint.uri()).matches()) {
					throw new RuntimeException("Improper URI format for " + c.getName() + "/" + method.getName());
				}

				consumer.accept(new FusionServiceContext(service, endpoint, method, i == methods.length - 1));
			}
		});
	}

	private static void saveServiceClient(String buffer, BaseService service) {

		String name = service.getClass().getSimpleName();

		String path = FUSION_CLIENT_PATH + name.replace("Service", "").toLowerCase() + ".js";

		File clientStubFile = new File(path);

		clientStubFile.mkdirs();
		try {
			if (clientStubFile.exists()) {
				clientStubFile.delete();
			}

			clientStubFile.createNewFile();

			Logger.get().trace("Saving service client for " + name + " to " + clientStubFile.getAbsolutePath());

			Utils.saveString(buffer, Files.newOutputStream(clientStubFile.toPath()));

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	static {
		RPCFactory.setPrependDomainVariableToUrl(false);
	}
}
