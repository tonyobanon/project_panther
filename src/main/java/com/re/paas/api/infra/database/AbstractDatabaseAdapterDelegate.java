package com.re.paas.api.infra.database;

import java.sql.Connection;

import com.re.paas.api.adapters.AbstractAdapterDelegate;

public abstract class AbstractDatabaseAdapterDelegate extends AbstractAdapterDelegate<DatabaseAdapter> {
	
	public abstract Connection getSQLDatabase(boolean loadConfigFile);
	
	public Connection getSQLDatabase() {
		return getSQLDatabase(false);
	}
	
	public abstract NoSQLInterface getNoSQLDatabase(NoSQLInterfaceType type, boolean loadConfigFile);
	
	public NoSQLInterface getNoSQLDatabase(NoSQLInterfaceType type) {
		return getNoSQLDatabase(type, false);
	}
	
}
