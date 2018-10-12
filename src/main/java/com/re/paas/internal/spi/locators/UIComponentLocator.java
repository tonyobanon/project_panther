package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.fusion.ui.AbstractComponent;
import com.re.paas.api.fusion.ui.AbstractUIComponentDelegate;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class UIComponentLocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.UI_COMPONENT;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Component");
	}

	@Override
	public Class<?> classType() {
		return AbstractComponent.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractUIComponentDelegate.class;
	}
	
}
