package com.re.paas.integrated.fusion.services;

import static com.re.paas.api.adapters.LoadPhase.MIGRATE;
import static com.re.paas.api.adapters.LoadPhase.PLATFORM_SETUP;
import static com.re.paas.api.clustering.slave.SlaveFunction.INGEST_ADAPTER_CONFIG;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AbstractAdapterDelegate;
import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.classes.ClusterDestination;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.fusion.FusionEndpoint;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.HttpStatusCodes;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.fusion.services.ServiceAffinity;
import com.re.paas.integrated.fusion.functionalities.SystemAdapterFunctionalities;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.clustering.model.IngestAdapterConfigRequest;
import com.re.paas.internal.clustering.model.IngestAdapterConfigResponse;
import com.re.paas.internal.fusion.imagineui.UIContext;
import com.re.paas.internal.utils.ObjectUtils;

public class SystemAdapterService extends BaseService {

	@Override
	public String uri() {
		return "/system-adapter";
	}

	@FusionEndpoint(uri = "types", functionality = SystemAdapterFunctionalities.Constants.GET_TYPES)
	public static void getTypes(RoutingContext ctx) {

		JsonArray res = new JsonArray();

		for (AdapterType type : AdapterType.values()) {
			res.add(type.toString());
		}

		ctx.response().end(res.encode());
	}

	@FusionEndpoint(uri = "descriptions", functionality = SystemAdapterFunctionalities.Constants.GET_DESCRIPTIONS)
	public static void getDescriptions(RoutingContext ctx) {

		AdapterType type = AdapterType.from(ctx.request().getParam("type"));

		AbstractAdapterDelegate<?, ? extends Adapter<?>> delegate = Singleton.get(type.getDelegateType());

		JsonObject res = new JsonObject();

		delegate.getAdapters().entrySet().forEach(s -> {

			String adapterName = s.getKey();
			Adapter<?> adapter = s.getValue();

			JsonObject spec = new JsonObject().put("title", adapter.title()).put("description", adapter.description())
					.put("iconUrl", adapter.iconUrl());

			res.put(adapterName, spec);
		});

		ctx.response().end(res.encode());
	}

	@FusionEndpoint(uri = "parameters", functionality = SystemAdapterFunctionalities.Constants.GET_PARAMETERS, affinity = ServiceAffinity.MASTER_ONLY)
	public static void getParameters(RoutingContext ctx) {

		AdapterType type = AdapterType.from(ctx.request().getParam("type"));
		String adapterName = ctx.request().getParam("name");

		@SuppressWarnings("rawtypes")
		AbstractAdapterDelegate delegate = Singleton.get(type.getDelegateType());
		Adapter<?> adapter = delegate.getAdapter(adapterName);

		String parameters = Json.getGson().toJson(adapter.initForm());

		ctx.response().end(parameters);
	}

	@FusionEndpoint(uri = "configure", method = HttpMethod.POST, functionality = SystemAdapterFunctionalities.Constants.CONFIGURE, affinity = ServiceAffinity.MASTER_ONLY)
	public static void configure(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		AdapterType type = AdapterType.from(ctx.request().getParam("type"));
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

		StringBuilder errMessage = new StringBuilder();

		if (response.equals(Boolean.TRUE)) {

			// Update other slaves in the cluster

			Map<String, CompletableFuture<IngestAdapterConfigResponse>> functionResult = Function.execute(ClusterDestination.OTHER_NODES,
					INGEST_ADAPTER_CONFIG, new IngestAdapterConfigRequest().addAdapterConfig(config),
					IngestAdapterConfigResponse.class);

			for (Entry<String, CompletableFuture<IngestAdapterConfigResponse>> e : functionResult.entrySet()) {

				String slaveAddress = e.getKey();
				Object status = e.getValue().join().getStatus().get(type);

				if (!status.equals(Boolean.TRUE)) {
					errMessage.append("Error occured while configuring " + type.toString() + " adapter, ")
							.append("slave = " + slaveAddress + ", ")
							.append("msg = '"
									+ (status instanceof Throwable ? ((Throwable) status).getLocalizedMessage()
											: status.toString())
									+ "'");
					break;
				}
			}

			if (errMessage.length() == 0) {
				return;
			}

		} else {
			errMessage.append("Error occured while configuring " + type.toString() + " adapter, ")
					.append("msg = " + (response instanceof Throwable ? ((Throwable) response).getLocalizedMessage()
							: response.toString()));
		}

		ctx.response().setStatusCode(HttpStatusCodes.SC_INTERNAL_SERVER_ERROR).end(errMessage.toString());
	}

}
