package com.re.paas.api.infra.metrics;

import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.SpiType;

public interface MetricsAdapter extends Adapter<Metrics>  {

	public static AbstractMetricsAdapterDelegate getDelegate() {
		return Singleton.get(AbstractMetricsAdapterDelegate.class);
	}
	
	Metrics getResource(Map<String, String> fields, String bucket);
	
	@Override
	default Metrics getResource(Map<String, String> fields) {
		return getResource(fields, null);
	}
	
	/**
	 * This closes the connection for all statsd clients
	 */
	public void close();
	
	@Override
	default AdapterType getType() {
		return AdapterType.METRICS;
	}
	
	@Override
	default SpiType getSpiType() {
		return SpiType.METRICS_ADAPTER;
	}
	
}
