package com.re.paas.internal.infra.database;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.database.DatabaseAdapter;
import com.re.paas.api.infra.database.NoSQLInterface;
import com.re.paas.api.infra.database.NoSQLInterfaceType;

public class DatabaseAdapterDelegate extends AbstractDatabaseAdapterDelegate {

	private static Connection sqlDatabase;
	private static Map<NoSQLInterfaceType, NoSQLInterface> nosqlDatabase = Collections
			.unmodifiableMap(new HashMap<>(NoSQLInterfaceType.values().length));

	@Override
	public Object load() {
		
		getSQLDatabase(true);
		
		for (NoSQLInterfaceType i : NoSQLInterfaceType.values()) {
			getNoSQLDatabase(i, true);
		}
		
		return true;
	}

	@Override
	public Connection getSQLDatabase(boolean loadConfigFile) {

		if (sqlDatabase != null && !loadConfigFile) {
			return sqlDatabase;
		}

		DatabaseAdapterConfig dbConfig = (DatabaseAdapterConfig) getConfig();

		DatabaseAdapter adapter = getAdapter(dbConfig.getAdapterName());
		Connection db = adapter.getSQLDatabase(dbConfig.getFields());

		sqlDatabase = db;
		return sqlDatabase;
	}

	@Override
	public NoSQLInterface getNoSQLDatabase(NoSQLInterfaceType type, boolean loadConfigFile) {

		if (nosqlDatabase.containsKey(type) && !loadConfigFile) {
			return nosqlDatabase.get(type);
		}

		DatabaseAdapterConfig dbConfig = (DatabaseAdapterConfig) getConfig();

		DatabaseAdapter adapter = getAdapter(dbConfig.getAdapterName());
		NoSQLInterface db = adapter.getNoSQLDatabase(type, dbConfig.getFields());

		nosqlDatabase.put(type, db);
		return db;
	}
}
