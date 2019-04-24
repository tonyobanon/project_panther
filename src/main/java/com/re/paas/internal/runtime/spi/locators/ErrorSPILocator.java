package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.errors.AbstractErrorSpiDelegate;
import com.re.paas.api.errors.Error;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class ErrorSPILocator extends BaseSPILocator {

	public ErrorSPILocator() {
		addTypeSuffix("Error");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.ERROR;
	}

	@Override
	public Class<? extends Resource> classType() {
		return Error.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractErrorSpiDelegate.class;
	}
	
}
