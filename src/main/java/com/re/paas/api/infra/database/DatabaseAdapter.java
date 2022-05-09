package com.re.paas.api.infra.database;

import com.re.paas.api.Adapter;
import com.re.paas.api.Singleton;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.runtime.spi.SpiType;

public interface DatabaseAdapter extends Adapter<Database> {

	public static AbstractDatabaseAdapterDelegate getDelegate() {
		return Singleton.get(AbstractDatabaseAdapterDelegate.class);
	}
	
	@Override
	default AdapterType getType() {
		return AdapterType.DATABASE;
	}
	
	@Override
	default SpiType getSpiType() {
		return SpiType.DATABASE_ADAPTER;
	}
}
