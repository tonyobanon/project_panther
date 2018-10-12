package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.errors.AbstractErrorSpiDelegate;
import com.re.paas.api.errors.Error;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class ErrorSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.ERROR;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Error");
	}

	@Override
	public Class<?> classType() {
		return Error.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractErrorSpiDelegate.class;
	}
	
}
