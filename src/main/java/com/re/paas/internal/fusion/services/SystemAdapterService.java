package com.re.paas.internal.fusion.services;

import static com.re.paas.api.clustering.slave.SlaveFunction.INGEST_ADAPTER_CONFIG;

import java.util.Map;
import java.util.Map.Entry;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AbstractAdapterDelegate;
import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.classes.ClusterDestination;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.HttpStatusCodes;
import com.re.paas.api.fusion.server.JsonArray;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.server.ServiceAffinity;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.clustering.objectmodels.IngestAdapterConfigRequest;
import com.re.paas.internal.clustering.objectmodels.IngestAdapterConfigResponse;
import com.re.paas.internal.fusion.functionalities.SystemAdapterFunctionalities;
import com.re.paas.internal.utils.ObjectUtils;

public class SystemAdapterService extends BaseService {

	@Override
	public String uri() {
		return "/system-adapter";
	}

	@FusionEndpoint(uri = "types", functionality = SystemAdapterFunctionalities.Constants.GET_TYPES)
	public void getTypes(RoutingContext ctx) {

		JsonArray res = new JsonArray();

		for (AdapterType type : AdapterType.values()) {
			res.add(type.toString());
		}

		ctx.response().end(res.encode());
	}

	@FusionEndpoint(uri = "descriptions", functionality = SystemAdapterFunctionalities.Constants.GET_DESCRIPTIONS)
	public void getDescriptions(RoutingContext ctx) {

		AdapterType type = AdapterType.from(ctx.request().getParam("type"));

		AbstractAdapterDelegate<? extends Adapter> delegate = Singleton.get(type.getDelegateType());

		JsonObject res = new JsonObject();

		delegate.getAdapters().entrySet().forEach(s -> {

			String adapterName = s.getKey();
			Adapter adapter = s.getValue();

			JsonObject spec = new JsonObject().put("title", adapter.title()).put("description", adapter.description())
					.put("iconUrl", adapter.iconUrl());

			res.put(adapterName, spec);
		});

		ctx.response().end(res.encode());
	}

	@FusionEndpoint(uri = "parameters", functionality = SystemAdapterFunctionalities.Constants.GET_PARAMETERS, affinity = ServiceAffinity.MASTER_ONLY)
	public void getParameters(RoutingContext ctx) {

		AdapterType type = AdapterType.from(ctx.request().getParam("type"));
		String adapterName = ctx.request().getParam("name");

		AbstractAdapterDelegate<? extends Adapter> delegate = Singleton.get(type.getDelegateType());
		Adapter adapter = delegate.getAdapter(adapterName);

		String parameters = Json.getGson().toJson(adapter.initForm());

		ctx.response().end(parameters);
	}

	@FusionEndpoint(uri = "configure", method = HttpMethod.POST, functionality = SystemAdapterFunctionalities.Constants.CONFIGURE, affinity = ServiceAffinity.MASTER_ONLY)
	public void configure(RoutingContext ctx) {

		JsonObject body = ctx.getBodyAsJson();

		AdapterType type = AdapterType.from(ctx.request().getParam("type"));
		String adapterName = body.getString("name");
		Map<String, String> fields = ObjectUtils.toStringMap(body.getJsonObject("fields").getMap());

		AbstractAdapterDelegate<? extends Adapter> delegate = Singleton.get(type.getDelegateType());

		AdapterConfig config = new AdapterConfig(type).setAdapterName(adapterName).setFields(fields);

		config.save();

		delegate.setConfig(config);

		Object response = delegate.load();

		StringBuilder errMessage = new StringBuilder();

		if (response.equals(Boolean.TRUE)) {

			// Update other slaves in the cluster

			Map<String, IngestAdapterConfigResponse> functionResult = Function.execute(ClusterDestination.OTHER_NODES,
					INGEST_ADAPTER_CONFIG, new IngestAdapterConfigRequest().addAdapterConfig(config),
					IngestAdapterConfigResponse.class);

			for (Entry<String, IngestAdapterConfigResponse> e : functionResult.entrySet()) {

				String slaveAddress = e.getKey();
				Object status = e.getValue().getStatus().get(type);

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
