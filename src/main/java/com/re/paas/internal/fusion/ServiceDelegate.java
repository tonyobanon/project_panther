package com.re.paas.internal.fusion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.fusion.Endpoint;
import com.re.paas.api.fusion.HttpServerResponse;
import com.re.paas.api.fusion.HttpStatusCodes;
import com.re.paas.api.fusion.Route;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.fusion.services.DefaultServiceAuthenticator;
import com.re.paas.api.fusion.services.ServiceAuthenticator;
import com.re.paas.api.fusion.services.ServiceDescriptor;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ExternalContext;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.runtime.ParameterizedInvokable;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.runtime.spi.ResourcesInitResult;
import com.re.paas.api.tasks.TaskModel;
import com.re.paas.internal.classes.ClassUtil;

@DelegateSpec
public class ServiceDelegate extends AbstractServiceDelegate {

	private static Logger LOG = Logger.get(ServiceDelegate.class);

	static final Integer DEFAULT_CACHE_MAX_AGE = 259200;

	public static Pattern uriPattern = Pattern.compile("\\A(\\Q/\\E[\\w]+([-]{1}[\\w]+)*)+\\z");

	private static final String SERVICE_DESCRIPTOR_RESOURCE_PREFIX = "svdsc-rp_";
	private static final String SERVICE_DESCRIPTOR_KEYS = "svdsck";

	private <S> List<S> getList(Class<S> T, String namespace) {
		@SuppressWarnings("unchecked")
		List<S> o = (List<S>) getLocalStore().get(namespace);
		return o;
	}

	private <S> void addToList(String namespace, S obj) {
		@SuppressWarnings("unchecked")
		List<S> e = (List<S>) getLocalStore().get(namespace);
		if (e == null) {
			e = new ArrayList<S>();
			getLocalStore().put(namespace, e);
		}
		if (!e.contains(obj)) {
			e.add(obj);
		}
	}

	@SuppressWarnings("unused")
	private Multimap<Route, ServiceDescriptor> getServiceDescriptors() {

		List<String> routes = getList(String.class, SERVICE_DESCRIPTOR_KEYS);

		Multimap<Route, ServiceDescriptor> result = LinkedHashMultimap.create(routes.size(), 2);

		routes.forEach(r -> {
			Route route = Route.fromString(r);
			ServiceDescriptor sDescriptor = this.getServiceDescriptor(SERVICE_DESCRIPTOR_RESOURCE_PREFIX + route);

			result.put(route, sDescriptor);
		});

		return result;
	}

	private ServiceDescriptor getServiceDescriptor(Route route) {
		return getServiceDescriptor(route.toString());
	}

	private ServiceDescriptor getServiceDescriptor(String route) {
		String namespace = (SERVICE_DESCRIPTOR_RESOURCE_PREFIX + route);
		return (ServiceDescriptor) getLocalStore().get(namespace);
	}

	private void setServiceDescriptor(Route route, ServiceDescriptor sDescriptor) {
		String namespace = SERVICE_DESCRIPTOR_RESOURCE_PREFIX + route.toString();
		getLocalStore().put(namespace, sDescriptor);
		addToList(SERVICE_DESCRIPTOR_KEYS, route.toString());
	}

	@Override
	public DelegateInitResult init() {

		// Then, add fusion services found in classpath

		scanAll(context -> {

			Class<?> serviceClass = context.getService().getClass();

			String methodName = context.getMethod().getName();

			// Get appId that owns this service
			String appId = ClassLoaders.getId(serviceClass);

			String uri = (context.getService().uri() + context.getEndpoint().uri()).replaceAll("(\\Q/\\E)+", "/");

			Route route = new Route(appId, uri, context.getEndpoint().method());

			if (this.getServiceDescriptor(route) != null) {
				Exceptions.throwRuntime("Route: " + route.toString() + " already exists");
			}

			ServiceDescriptor sDescriptor = new ServiceDescriptor(serviceClass.getName(), methodName,
					context.getEndpoint());

			LOG.trace("Setting service descriptor for route: " + route.toString());

			setServiceDescriptor(route, sDescriptor);
		});

		return DelegateInitResult.SUCCESS;
	}

	@Override
	public void handler(RoutingContext ctx) {

		Route route = ctx.route();

		// Find all matching handlers

		List<ServiceDescriptor> sDescriptors = new ArrayList<>();

		sDescriptors.add(getServiceDescriptor(new Route(route.getAppId())));

		sDescriptors.add(getServiceDescriptor(new Route(route.getAppId()).setMethod(route.getMethod())));

		sDescriptors.add(getServiceDescriptor(new Route(route.getAppId()).setUri(route.getUri())));

		sDescriptors.add(getServiceDescriptor(route));

		sDescriptors = sDescriptors.stream().filter(s -> s != null).collect(Collectors.toList());

		if (sDescriptors.isEmpty()) {

			// No matching service descriptor was found for this route, return 404

			ctx.response()
			.setStatusCode(HttpStatusCodes.SC_NOT_FOUND);

			return;
		}

		// Authenticate

		for (ServiceDescriptor sd : sDescriptors) {

			Class<? extends ServiceAuthenticator> clazz = sd.getEndpoint().customAuthenticator();

			if (clazz == DefaultServiceAuthenticator.class) {

				// No need to create new instance and check, since we know it always returns
				// true
				LOG.trace("Skipping authentication for: " + sd);
				continue;
			}

			ServiceAuthenticator authenticator = ClassUtil.createInstance(clazz);

			if (!authenticator.authenticate(ctx)) {
				ctx.response().setStatusCode(HttpStatusCodes.SC_UNAUTHORIZED);
			}
		}

		// Construct ParameterizedExecutable instance that we can pass on to any
		// available node in the cluster to service

		ClassLoader cl = ClassLoaders.getClassLoader(route.getAppId());

		ParameterizedExecutable<RoutingContext, HttpServerResponse> executable = buildExecutable(cl, sDescriptors, ctx);

		if (distributeTrafficOnMaster) {

			HttpServerResponse response = TaskModel.getDelegate().execute(executable).join();

			// Re-ingest the response of this http request
			((RoutingContextImpl) ctx).setResponse(response);

		} else {
			executable.getFunction().apply(executable.getParameter());
		}
	}

	private static ParameterizedExecutable<RoutingContext, HttpServerResponse> buildExecutable(ClassLoader cl,
			List<ServiceDescriptor> sDescriptors, RoutingContext ctx) {

		String appId = ClassLoaders.getId(cl);

		ParameterizedInvokable<RoutingContext, HttpServerResponse> i = (context) -> {

			Handlers.defaultHeadHandler(appId).accept(context);

			for (ServiceDescriptor sDescriptor : sDescriptors) {

				try {

					Class<?> clazz = ClassLoaders.getClassLoader(appId)
							.loadClass(RoutingContextHandler.class.getName());

					Class<?>[] argumentTypes = new Class<?>[] { ServiceDescriptor.class, RoutingContext.class };
					Method m = clazz.getDeclaredMethod("handle", argumentTypes);

					m.invoke(null, sDescriptor, context);

				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					Exceptions.throwRuntime(e);
				}

				if (context.response().ended()) {
					break;
				}
			}

			Handlers.defaultTailHandler(appId).accept(context);

			return context.response();
		};

		return ExecutorFactory.get().buildFunction(new ObjectWrapper<ClassLoader>(cl), i, ctx, new ExternalContext(appId, true, sDescriptors.get(0).getEndpoint().affinity()));
	}

	/**
	 * This discovers fusion services by scanning the classpath
	 */
	private void scanAll(Consumer<FusionServiceContext> consumer) {

		LOG.trace("Scanning for services");

		ResourcesInitResult result = this.addResources(c -> {

			final BaseService service = com.re.paas.internal.classes.ClassUtil.createInstance(c);
			
			if (!service.uri().isEmpty() && !service.uri().equals("/")
					&& !uriPattern.matcher(service.uri()).matches()) {
				return ResourceStatus.ERROR.setMessage("Improper URI format for " + c.getName());
			}

			List<Method> methodsList = new ArrayList<Method>();

			for (Method m : c.getDeclaredMethods()) {

				if (m.isSynthetic()) {
					// Note: Lambda functions are compiled as synthetic members of the declaring
					// class, and some private helper methods may be contained in Service classes
					continue;
				}

				if (!m.isAnnotationPresent(Endpoint.class)) {
					continue;
				}

				int modifiers = m.getModifiers();

				if ((!Modifier.isStatic(modifiers)) || (!Modifier.isPublic(modifiers))) {
					continue;
				}

				if (m.getReturnType() != void.class) {
					continue;
				}
				
				if (m.getParameterTypes().length != 1 || m.getParameterTypes()[0] != RoutingContext.class) {
					continue;
				}

				methodsList.add(m);
			}
			
			Boolean allUniqueNames = methodsList.stream().map(m -> m.getName()).allMatch(new HashSet<>()::add);

			if (!allUniqueNames) {
				return ResourceStatus.ERROR.setMessage("Duplicate method name found");
			}

			Method[] methods = methodsList.toArray(new Method[methodsList.size()]);

			for (int i = 0; i < methods.length; i++) {

				Method method = methods[i];

				Endpoint endpoint = method.getAnnotation(Endpoint.class);

				if (!endpoint.uri().isEmpty() && !endpoint.uri().equals("/")
						&& !uriPattern.matcher(endpoint.uri()).matches()) {

					return ResourceStatus.ERROR
							.setMessage("Improper URI format for " + c.getName() + "#" + method.getName());
				}

				consumer.accept(new FusionServiceContext(service, endpoint, method, i == methods.length - 1));
			}

			return ResourceStatus.UPDATED;

		});
		
		
		result.getErrors().forEach(error -> {
			LOG.error(error.getCulprit() + ": " + error.getErrorMessage());
		});
		
	}
}
