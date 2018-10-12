package com.re.paas.api.clustering;

import com.re.paas.api.spi.SpiDelegate;

public abstract class AbstractFunctionDelegate extends SpiDelegate<Function> {

	public abstract Function getFunction(String namespace, Short contextId);	
	
	public abstract Function getFunction(Short id);	
	
	public abstract Short getId(Function id);	
	
}
