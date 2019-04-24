package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.clustering.AbstractFunctionDelegate;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class FunctionSPILocator extends BaseSPILocator {

	public FunctionSPILocator() {
		addTypeSuffix("Function");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.FUNCTION;
	}

	@Override
	public Class<? extends Resource> classType() {
		return Function.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractFunctionDelegate.class;
	}
}
