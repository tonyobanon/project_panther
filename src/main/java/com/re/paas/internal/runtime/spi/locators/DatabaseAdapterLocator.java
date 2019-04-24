package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.database.DatabaseAdapter;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class DatabaseAdapterLocator extends BaseSPILocator {

	public DatabaseAdapterLocator() {
		addTypeSuffix("Adapter");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.DATABASE_ADAPTER;
	}

	@Override
	public Class<? extends Resource> classType() {
		return DatabaseAdapter.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractDatabaseAdapterDelegate.class;
	}
	
}
