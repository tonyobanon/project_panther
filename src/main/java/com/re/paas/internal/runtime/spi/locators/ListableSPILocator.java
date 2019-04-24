package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.listable.AbstractListableDelegate;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class ListableSPILocator extends BaseSPILocator {

	public ListableSPILocator() {
		addTypeSuffix("List");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.LISTABLE;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return Listable.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractListableDelegate.class;
	}
	
}
