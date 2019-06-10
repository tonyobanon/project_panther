package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class FunctionalitySPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.FUNCTIONALITY;
	}

	@Override
	public Class<? extends Resource> classType() {
		return Functionality.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractFunctionalityDelegate.class;
	}
	
}
