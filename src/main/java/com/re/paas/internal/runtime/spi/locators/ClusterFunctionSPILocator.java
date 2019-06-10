package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.AbstractClusterFunctionDelegate;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class ClusterFunctionSPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.CLUSTER_FUNCTION;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return AbstractClusterFunction.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractClusterFunctionDelegate.class;
	}
}
