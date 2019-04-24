package com.re.paas.internal.boostrapping.clustering.slave;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AbstractAdapterDelegate;
import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.slave.SlaveFunction;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.clustering.SlaveNodeRole;
import com.re.paas.internal.clustering.objectmodels.IngestAdapterConfigRequest;
import com.re.paas.internal.clustering.objectmodels.IngestAdapterConfigResponse;

public class IngestAdapterConfigFunction
		extends AbstractClusterFunction<IngestAdapterConfigRequest, IngestAdapterConfigResponse> {

	@Override
	public Class<? extends NodeRole> role() {
		return SlaveNodeRole.class;
	}

	@Override
	public Function id() {
		return SlaveFunction.INGEST_ADAPTER_CONFIG;
	}

	@Override
	public IngestAdapterConfigResponse delegate(IngestAdapterConfigRequest t) {

		IngestAdapterConfigResponse response = new IngestAdapterConfigResponse();

		for (AdapterConfig config : t.getAdapterConfig()) {

			Logger.get().info("Ingesting configuration for " + config.getType() + " adapter");

			AbstractAdapterDelegate<?, ? extends Adapter<?>> delegate = Singleton.get(config.getType().getDelegateType());

			delegate.setConfig(config);

			response.addStatus(config.getType(), delegate.load(LoadPhase.START));
		}
		return response;
	}

}
