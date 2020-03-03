package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.fusion.ui.deprecated.AbstractComponent;
import com.re.paas.api.fusion.ui.deprecated.AbstractUIComponentDelegate;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class UIComponentLocator extends BaseSPILocator {

	@Override
	public SpiType spiType() {
		return SpiType.UI_COMPONENT;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return AbstractComponent.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractUIComponentDelegate.class;
	}
	
}
