package com.re.paas.api.clustering;

import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractClusterFunctionDelegate extends SpiDelegate<AbstractClusterFunction<Object, Object>> {
	
	public abstract AbstractClusterFunction<Object, Object> getClusterFunction(Function function);
	
}
