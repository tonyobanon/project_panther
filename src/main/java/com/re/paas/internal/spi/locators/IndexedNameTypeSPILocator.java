package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.listable.AbstractIndexedNameTypeDelegate;
import com.re.paas.api.listable.IndexedNameType;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class IndexedNameTypeSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.INDEXED_NAME_TYPE;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("NameTypes");
	}

	@Override
	public Class<?> classType() {
		return IndexedNameType.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractIndexedNameTypeDelegate.class;
	}
	
}
