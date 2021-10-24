package com.re.paas.internal.fusion.services;

import static com.re.paas.api.adapters.LoadPhase.MIGRATE;
import static com.re.paas.api.adapters.LoadPhase.PLATFORM_SETUP;

import java.util.Map;
import java.util.function.BiConsumer;

import org.infinispan.manager.DefaultCacheManager;

import com.re.paas.api.Adapter;
import com.re.paas.api.Singleton;
import com.re.paas.api.adapters.AbstractAdapterDelegate;
import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.fusion.Endpoint;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.HttpStatusCodes;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.fusion.UIContext;
import com.re.paas.internal.runtime.spi.AppDelegate;
import com.re.paas.internal.utils.ObjectUtils;

public class SystemAdapterService extends BaseService {

	@Override
	public String uri() {
		return "/system-adapter";
	}
	
	@Endpoint(uri = "/hooks/shutdown")
	public static void shutdown(RoutingContext ctx) {

		AppDelegate.shutdown();
		
		ClusteringServices cService = ClusteringServices.get();

		if (cService.isExecutioner()) {
			
			DefaultCacheManager cm = (DefaultCacheManager) cService.getCacheManager();
			
			cm.executor().singleNodeSubmission().execute(() -> {
				ClusteringServices.get().assumeExecutioner();
			});
		}
	}

	@Endpoint(uri = "/adapters/types")
	public static void getTypes(RoutingContext ctx) {

		JsonArray res = new JsonArray();

		for (AdapterType type : AdapterType.values()) {
			if (!type.isPlatformIntrinsic()) {
				res.add(type.toString());
			}
		}

		ctx.response().writeHtml(res.encode());
	}

	@Endpoint(uri = "/adapters/descriptions")
	public static void getDescriptions(RoutingContext ctx) {

		AdapterType type = AdapterType.from(ctx.request().getParameter("type"));

		AbstractAdapterDelegate<?, ? extends Adapter<?>> delegate = Singleton.get(type.getDelegateType());

		JsonObject res = new JsonObject();

		delegate.getAdapters().entrySet().forEach(s -> {

			String adapterName = s.getKey();
			Adapter<?> adapter = s.getValue();

			JsonObject spec = new JsonObject().put("title", adapter.title()).put("description", adapter.description())
					.put("iconUrl", adapter.iconUrl());

			res.put(adapterName, spec);
		});

		ctx.response().writeHtml(res.encode());
	}

	@Endpoint(uri = "/adapters/parameters", affinity = Affinity.ANY)
	public static void getParameters(RoutingContext ctx) {

		AdapterType type = AdapterType.from(ctx.request().getParameter("type"));
		String adapterName = ctx.request().getParameter("name");

		@SuppressWarnings("rawtypes")
		AbstractAdapterDelegate delegate = Singleton.get(type.getDelegateType());
		Adapter<?> adapter = delegate.getAdapter(adapterName);

		String parameters = Json.getGson().toJson(adapter.initForm());

		ctx.response().writeHtml(parameters);
	}

	@Todo("Remember to save .installed file, when user clicks on finish")
	@Endpoint(uri = "/adapters/configure", method = HttpMethod.POST, affinity = Affinity.ANY)
	public static void configure(RoutingContext ctx) {

		JsonObject body = ctx.request().getBodyAsJson();

		AdapterType type = AdapterType.from(ctx.request().getParameter("type"));
		String adapterName = body.getString("name");
		Map<String, String> fields = ObjectUtils.toStringMap(body.getJsonObject("fields").getMap());

		@SuppressWarnings("unchecked")
		AbstractAdapterDelegate<Object, ? extends Adapter<?>> delegate = (AbstractAdapterDelegate<Object, ? extends Adapter<?>>) Singleton
				.get(type.getDelegateType());

		AdapterConfig config = new AdapterConfig(type).setAdapterName(adapterName).setFields(fields);

		Object currentResource = null;
		LoadPhase loadPhase = null;

		try {
			currentResource = delegate.getAdapter().getResource(delegate.getConfig().getFields());

			// If this is a new installation, a call to getConfig() in delegate.getAdapter()
			// should return null,
			// and a NullpointerException will be thrown

			// Since a configuration file already exists, we need to do a migration
			loadPhase = MIGRATE;

		} catch (NullPointerException e) {

			// Since a configuration file does not already exist, we need to do a setup
			loadPhase = PLATFORM_SETUP;
		}

		config.save();

		delegate.setConfig(config);

		Object response = delegate.load(loadPhase);

		if (loadPhase == MIGRATE && delegate.requiresMigration()) {

			// Run adapter migration
			BiConsumer<Integer, String> loadContext = UIContext.loading("Running " + type + " migration", true);

			delegate.migrate(currentResource, loadContext);
		}

		if (!response.equals(Boolean.TRUE)) {
			String err = new StringBuilder().append("Error occured while configuring " + type.toString() + " adapter, ")
					.append("msg = " + (response instanceof Throwable ? ((Throwable) response).getLocalizedMessage()
							: response.toString()))
					.toString();
			
			ctx.response().setStatus(HttpStatusCodes.SC_INTERNAL_SERVER_ERROR).writeHtml(err);
		}

	}

}
