package com.re.paas.internal.clustering.objectmodels;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.adapters.AdapterConfig;
import com.re.paas.api.adapters.AdapterType;

public class IngestAdapterConfigRequest {

	private List<AdapterConfig> adapterConfig = new ArrayList<>(AdapterType.values().length);

	public IngestAdapterConfigRequest setAdapterConfig(List<AdapterConfig> adapterConfig) {
		this.adapterConfig = adapterConfig;
		return this;
	}

	public List<AdapterConfig> getAdapterConfig() {
		return adapterConfig;
	}

	public IngestAdapterConfigRequest addAdapterConfig(AdapterConfig config) {
		this.adapterConfig.add(config);
		return this;
	}

}
