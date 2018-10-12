package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.templating.AbstractObjectModelFactorySpiDelegate;
import com.re.paas.api.templating.TemplateObjectModelFactory;

public class TemplateObjectModelFactorySPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.TEMPLATE_OBJECT_MODEL_FACTORY;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("TemplateFactory");
	}

	@Override
	public Class<?> classType() {
		return TemplateObjectModelFactory.class;
	}

	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractObjectModelFactorySpiDelegate.class;
	}
	
}
