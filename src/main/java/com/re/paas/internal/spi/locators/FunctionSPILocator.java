package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.clustering.AbstractFunctionDelegate;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class FunctionSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.FUNCTION;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Function");
	}

	@Override
	public Class<?> classType() {
		return Function.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractFunctionDelegate.class;
	}
}
