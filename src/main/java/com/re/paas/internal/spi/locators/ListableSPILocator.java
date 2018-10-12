package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.listable.AbstractListableDelegate;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class ListableSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.LISTABLE;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("List");
	}

	@Override
	public Class<?> classType() {
		return Listable.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractListableDelegate.class;
	}
	
}
