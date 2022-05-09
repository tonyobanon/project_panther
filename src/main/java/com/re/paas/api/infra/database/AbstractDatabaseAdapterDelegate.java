package com.re.paas.api.infra.database;

import com.re.paas.api.adapters.AbstractAdapterDelegate;
import com.re.paas.api.infra.database.document.Database;

public abstract class AbstractDatabaseAdapterDelegate extends AbstractAdapterDelegate<Database, DatabaseAdapter> {

	public abstract Database getDatabase();

	@Override
	public final boolean requiresMigration() {
		return true;
	}
	
	@Override
	public final Class<?> getLocatorClassType() {
		return DatabaseAdapter.class;
	}
}
