package com.re.paas.api.infra.database;

import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.runtime.spi.SpiType;

public interface DatabaseAdapter extends Adapter<Database> {

	public static AbstractDatabaseAdapterDelegate getDelegate() {
		return Singleton.get(AbstractDatabaseAdapterDelegate.class);
	}
	
	default Database getDatabase(Map<String, String> fields) {
		return getResource(fields);
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
