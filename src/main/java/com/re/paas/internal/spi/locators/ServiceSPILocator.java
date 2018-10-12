package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class ServiceSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.SERVICE;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Service");
	}

	@Override
	public Class<?> classType() {
		return BaseService.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractServiceDelegate.class;
	}
	
}
