package com.re.paas.api.infra.metrics;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractMetricsAdapterDelegate extends AbstractAdapterDelegate<Metrics, MetricsAdapter> {

	public abstract Metrics getMetrics(String bucket);
	
	public Metrics getMetrics() {
		return getMetrics(null);
	}

	@Override
	public final boolean requiresMigration() {
		return true;
	}

}
