package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.fusion.services.AbstractFunctionalityDelegate;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class FunctionalitySPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.FUNCTIONALITY;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Functionalities");
	}

	@Override
	public Class<?> classType() {
		return Functionality.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractFunctionalityDelegate.class;
	}
	
}
