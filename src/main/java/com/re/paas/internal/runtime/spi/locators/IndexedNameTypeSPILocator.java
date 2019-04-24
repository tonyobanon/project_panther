package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.listable.AbstractIndexedNameTypeDelegate;
import com.re.paas.api.listable.IndexedNameType;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class IndexedNameTypeSPILocator extends BaseSPILocator {

	public IndexedNameTypeSPILocator() {
		addTypeSuffix("NameTypes");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.INDEXED_NAME_TYPE;
	}

	@Override
	public Class<? extends Resource> classType() {
		return IndexedNameType.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractIndexedNameTypeDelegate.class;
	}
	
}
