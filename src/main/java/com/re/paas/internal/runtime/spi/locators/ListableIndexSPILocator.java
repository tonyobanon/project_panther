package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.listable.AbstractListableIndexDelegate;
import com.re.paas.api.listable.ListableIndex;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class ListableIndexSPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.LISTABLE_INDEX;
	}

	@Override
	public Class<? extends Resource> classType() {
		return ListableIndex.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractListableIndexDelegate.class;
	}
	
}
