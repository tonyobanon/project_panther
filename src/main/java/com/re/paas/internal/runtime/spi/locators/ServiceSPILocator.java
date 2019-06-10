package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class ServiceSPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.SERVICE;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return BaseService.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractServiceDelegate.class;
	}
	
}
