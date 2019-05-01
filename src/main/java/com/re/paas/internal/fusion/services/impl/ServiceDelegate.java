package com.re.paas.internal.fusion.services.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import com.re.paas.api.app_provisioning.AppClassLoader.DelegationType;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.DefaultServiceAuthenticator;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.Route;
import com.re.paas.api.fusion.server.ServiceDescriptor;
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

	static final Integer DEFAULT_CACHE_MAX_AGE = 259200;

	private static final String FUSION_CLIENT_PATH = System.getProperty("java.io.tmpdir") + File.separator
			+ Platform.getPlatformPrefix() + File.separator + "fusion-service-clients" + File.separator;

	public static Pattern endpointClassUriPattern = Pattern
			.compile("\\A\\Q/\\E[a-zA-Z]+[-]*[a-zA-Z]+(\\Q/\\E[a-zA-Z]+[-]*[a-zA-Z]+)*\\z");

	public static Pattern endpointMethodUriPattern = Pattern.compile("\\A\\Q/\\E[a-zA-Z-]+[-]*[a-zA-Z]+\\z");

	private static final String SERVICE_DESCRIPTOR_RESOURCE_PREFIX = "svdsc-rp_";
	private static final String SERVICE_DESCRIPTOR_KEYS = "svdsck";

	private static final String SERVICE_FUNCTIONALITY_RESOURCE_PREFIX = "svf-rp_";
	private static final String FUNCTIONALITY_SERVICE_RESOURCE_PREFIX = "fsv-rp_";

	protected static final String USER_ID_PARAM_NAME = "x_uid";
	public static final String BASE_PATH = "/api";

	@Override
	public Multimap<Route, ServiceDescriptor> getServiceDescriptors() {

		List<String> routes = getList(String.class, SERVICE_DESCRIPTOR_KEYS);

		Multimap<Route, ServiceDescriptor> result = LinkedHashMultimap.create(routes.size(), 2);

		routes.forEach(r -> {
			Route route = Route.fromString(r);
			ServiceDescriptor sDescriptor = this.getServiceDescriptor(SERVICE_DESCRIPTOR_RESOURCE_PREFIX + route);

			result.put(route, sDescriptor);
		});

		return result;
	}

	@Override
	public ServiceDescriptor getServiceDescriptor(Route route) {
		return getServiceDescriptor(route.toString());
	}

	private ServiceDescriptor getServiceDescriptor(String route) {
		String namespace = (SERVICE_DESCRIPTOR_RESOURCE_PREFIX + route);
		return (ServiceDescriptor) get(namespace);
	}

	private void setServiceDescriptor(Route route, ServiceDescriptor sDescriptor) {
		String namespace = SERVICE_DESCRIPTOR_RESOURCE_PREFIX + route.toString();
		set(namespace, sDescriptor);
		addToList(SERVICE_DESCRIPTOR_KEYS, route.toString());
	}

	@Override
	public Functionality getServiceFunctionality(Route route) {
		String namespace = (SERVICE_FUNCTIONALITY_RESOURCE_PREFIX + route.toString());
		return Functionality.fromString(get(namespace).toString());
	}

	private void setServiceFunctionality(String route, Functionality functionality) {
		String namespace = SERVICE_FUNCTIONALITY_RESOURCE_PREFIX + route;
		set(namespace, Functionality.toString(functionality));
	}

	@Override
	public List<String> getFunctionalityService(Functionality functionality) {
		String namespace = (FUNCTIONALITY_SERVICE_RESOURCE_PREFIX + Functionality.toString(functionality));
		return getList(String.class, namespace);
	}

	private void addFunctionalityService(Functionality functionality, String route) {
		String namespace = FUNCTIONALITY_SERVICE_RESOURCE_PREFIX + Functionality.toString(functionality);
		addToList(namespace, route);
	}

	@Override
	public DelegateInitResult init() {

		CacheAdapter.start();

		Logger.get().debug("Scanning for API routes");

		// Sample code to add a handler to a particular route
		// addRouteHandler(new Route(), new RouteHandler((ctx) -> {} , false));

		// Then, add fusion services found in classpath

		ObjectWrapper<StringBuilder> serviceCientBuffer = new ObjectWrapper<StringBuilder>().set(new StringBuilder());

		// This is used to avoid duplicate service methods, since they all exists in a
		// global client context
		final Map<String, String> methodNames = new HashMap<>();

		scanAll(context -> {

			Class<?> serviceClass = context.getService().getClass();

			String methodName = context.getMethod().getName();

			if (methodNames.containsKey(methodName)) {

				String msg = "Method name: " + methodName + "(..) in " + serviceClass.getSimpleName()
						+ " already exists in " + methodNames.get(methodName);

				throw new ResourceException(ResourceException.RESOURCE_ALREADY_EXISTS, msg);
			}

			methodNames.put(methodName, serviceClass.getName());

			Functionality functionality = Functionality.fromString(context.getEndpoint().functionality());

			// Get appId that owns this service
			String appId = serviceClass.getClassLoader() instanceof AppClassLoader
					? ((AppClassLoader) serviceClass.getClassLoader()).getAppId()
					: AppProvisioner.DEFAULT_APP_ID;

			String uri = "/" + appId + context.getService().uri() + context.getEndpoint().uri();

			HttpMethod httpMethod = context.getEndpoint().method();

			Route route = new Route(uri, HttpMethod.valueOf(httpMethod.name()));

			if (this.getServiceDescriptor(route) != null) {
				Exceptions.throwRuntime("A service already exists with uri: " + uri);
			}

			ServiceDescriptor sDescriptor = new ServiceDescriptor(serviceClass.getName(), methodName,
					context.getEndpoint());

			Logger.get().trace("Setting service descriptor for uri: " + uri);

			setServiceDescriptor(route, sDescriptor);

			Logger.get().trace("Setting AppClassLoader delegationType for " + serviceClass.getName() + " to "
					+ DelegationType.FIND_FIRST.toString());

			AppClassLoader.addDelegationType(serviceClass.getName(), DelegationType.FIND_FIRST);

			Logger.get().trace("Mapping route: " + uri + " (" + httpMethod + ") to functionality: " + functionality);

			setServiceFunctionality(route.toString(), functionality);

			addFunctionalityService(functionality, uri);

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
				
				if (m.isSynthetic()) {
					// Note: Lambda functions are compiled as synthetic members of the declaring
					// class, and some private helper methods may be contained in Service classes
					continue;
				}
				
				if (!m.isAnnotationPresent(FusionEndpoint.class)) {
					continue;
				}

				int modifiers = m.getModifiers();

				if ((!Modifier.isStatic(modifiers)) || (!Modifier.isPublic(modifiers))) {
					continue;
				}
				
				methodsList.add(m);
			}

			Method[] methods = methodsList.toArray(new Method[methodsList.size()]);

			for (int i = 0; i < methods.length; i++) {

				Method method = methods[i];

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
