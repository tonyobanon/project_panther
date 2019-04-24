package com.re.paas.api.infra.database;

import com.re.paas.api.adapters.AbstractAdapterDelegate;
import com.re.paas.api.infra.database.document.Database;

public abstract class AbstractDatabaseAdapterDelegate extends AbstractAdapterDelegate<Database, DatabaseAdapter> {
	
	public abstract Database getDatabase(boolean loadConfigFile);
	
	public Database getDatabase() { 
		return getDatabase(false);
	}
	
	@Override
	public final boolean requiresMigration() {
		return true;
	}
}
