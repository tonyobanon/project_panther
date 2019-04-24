package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.templating.AbstractObjectModelFactorySpiDelegate;
import com.re.paas.api.templating.TemplateObjectModelFactory;

public class TemplateObjectModelFactorySPILocator extends BaseSPILocator {

	public TemplateObjectModelFactorySPILocator() {
		addTypeSuffix("TemplateFactory");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.TEMPLATE_OBJECT_MODEL_FACTORY;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return TemplateObjectModelFactory.class;
	}

	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractObjectModelFactorySpiDelegate.class;
	}
	
}
