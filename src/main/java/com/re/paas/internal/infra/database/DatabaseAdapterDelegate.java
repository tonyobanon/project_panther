package com.re.paas.internal.infra.database;

import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;

@DelegateSpec(dependencies = SpiType.NODE_ROLE)
public class DatabaseAdapterDelegate extends AbstractDatabaseAdapterDelegate {

	private static Database database;

	@Override
	public Boolean load(LoadPhase phase) {
		Database db = getDatabase(true);

		if (AbstractRole.getDelegate().isMaster()) {
			switch (phase) {
			case PLATFORM_SETUP: // Needs db.load as newly created artifacts may need to be loaded
				setupTables(); 
				break;
			case START: // Needs db.load as existing artifacts may need to be loaded
				updateSchemas(); 
				break;
			case MIGRATE: // Does not need db.load as no artifacts exists
				break;
			}
			db.load();
		}
		return true;
	}

	@Override
	public Database getDatabase(boolean loadConfigFile) {

		if (database != null && !loadConfigFile) {
			return database;
		}

		Database db = getAdapter().getDatabase(getConfig().getFields());

		database = db;
		return database;
	}

	private void setupTables() {
		// auto creation of new table(s)
	}

	private void updateSchemas() {
		// update existing tables (if necessary)
	}

	@Override
	@BlockerTodo
	public void migrate(Database outgoing, BiConsumer<Integer, String> listener) {

		// Create tables

		// Create Indexes

		// Scan each table
	}
}
