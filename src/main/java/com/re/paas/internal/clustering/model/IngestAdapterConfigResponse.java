package com.re.paas.internal.clustering.model;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.adapters.AdapterType;

public class IngestAdapterConfigResponse {

	private Map<AdapterType, Object> status = new HashMap<>();

	public Map<AdapterType, Object> getStatus() {
		return status;
	}
	
	public IngestAdapterConfigResponse addStatus(AdapterType type, Object status) {
		this.status.put(type, status);
		return this;
	}

	public IngestAdapterConfigResponse setStatus(Map<AdapterType, Object> status) {
		this.status = status;
		return this;
	}
	
}
