package com.re.paas.internal.infra.database;

import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;

@DelegateSpec(dependencies = SpiType.NODE_ROLE)
public class DatabaseAdapterDelegate extends AbstractDatabaseAdapterDelegate {

	private static Database database;

	@Override
	public Boolean load(LoadPhase phase) {
		DatabaseImpl db = (DatabaseImpl) getDatabase(true);
		return db.load(phase);
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

	@Override
	@BlockerTodo
	public void migrate(Database outgoing, BiConsumer<Integer, String> listener) {
		// Todo: migrate tables and indexes
	}
}
