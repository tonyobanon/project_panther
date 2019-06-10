package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.templating.AbstractObjectModelSpiDelegate;
import com.re.paas.api.templating.TemplateObjectModel;

public class TemplateObjectModelSPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.TEMPLATE_OBJECT_MODEL;
	}

	@Override
	public ClassIdentityType classIdentity() {
		return ClassIdentityType.ASSIGNABLE_FROM;
	}
	
	@Override
	public Class<? extends AbstractResource> classType() {
		return TemplateObjectModel.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractObjectModelSpiDelegate.class;
	}
	
}
