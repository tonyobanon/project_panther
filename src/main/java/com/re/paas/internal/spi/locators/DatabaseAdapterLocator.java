package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.database.DatabaseAdapter;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class DatabaseAdapterLocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.DATABASE_ADAPTER;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Adapter");
	}

	@Override
	public Class<?> classType() {
		return DatabaseAdapter.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractDatabaseAdapterDelegate.class;
	}
	
}
