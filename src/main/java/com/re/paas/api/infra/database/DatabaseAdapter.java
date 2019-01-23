package com.re.paas.api.infra.database;

import java.sql.Connection;
import java.util.Map;

import com.re.paas.api.Adapter;
import com.re.paas.api.adapters.AdapterType;
import com.re.paas.api.designpatterns.Singleton;

public interface DatabaseAdapter extends Adapter {

	public static AbstractDatabaseAdapterDelegate getDelegate() {
		return Singleton.get(AbstractDatabaseAdapterDelegate.class);
	}
	
	public abstract Connection getSQLDatabase(Map<String, String> fields);

	public abstract NoSQLInterface getNoSQLDatabase(NoSQLInterfaceType type, Map<String, String> fields);
	
	@Override
	default AdapterType getType() {
		return AdapterType.DATABASE;
	}
}
