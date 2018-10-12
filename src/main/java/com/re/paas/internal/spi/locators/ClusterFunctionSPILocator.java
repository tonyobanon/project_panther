package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.clustering.AbstractClusterFunctionDelegate;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.clustering.AbstractClusterFunction;

public class ClusterFunctionSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.CLUSTER_FUNCTION;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Function");
	}

	@Override
	public Class<?> classType() {
		return AbstractClusterFunction.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractClusterFunctionDelegate.class;
	}
}
