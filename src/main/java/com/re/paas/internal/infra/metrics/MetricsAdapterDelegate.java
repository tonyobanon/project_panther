package com.re.paas.internal.infra.metrics;

import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.metrics.AbstractMetricsAdapterDelegate;
import com.re.paas.api.infra.metrics.Metrics;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;

@DelegateSpec(dependencies = SpiType.NODE_ROLE)
public class MetricsAdapterDelegate extends AbstractMetricsAdapterDelegate {

	@Override
	public Boolean load(LoadPhase phase) {
		return true;
	}

	@Override
	public Metrics getMetrics(String bucket) {
		return getAdapter().getResource(getConfig().getFields(), bucket);
	}

	@Override
	@BlockerTodo
	public void migrate(Metrics outgoing, BiConsumer<Integer, String> listener) {
	}
	
	@Override
	public void shutdown() {
		getAdapter().close();
	}
}
