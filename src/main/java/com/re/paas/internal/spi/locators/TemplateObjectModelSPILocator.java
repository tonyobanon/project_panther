package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.ClassIdentityType;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.templating.AbstractObjectModelSpiDelegate;
import com.re.paas.api.templating.TemplateObjectModel;

public class TemplateObjectModelSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.TEMPLATE_OBJECT_MODEL;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Template");
	}

	@Override
	public ClassIdentityType classIdentity() {
		return ClassIdentityType.ASSIGNABLE_FROM;
	}
	
	@Override
	public Class<?> classType() {
		return TemplateObjectModel.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractObjectModelSpiDelegate.class;
	}
	
}
