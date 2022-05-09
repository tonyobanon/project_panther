package com.re.paas.internal.infra.database;

import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.database.document.Database;

public class DatabaseAdapterDelegate extends AbstractDatabaseAdapterDelegate {

	private Database database;

	@Override
	public Boolean load(LoadPhase phase) {
		this.database = getAdapter().getResource(getConfig().getFields());
		return true;
	}

	@Override
	public Database getDatabase() {
		return this.database;
	}

	@Override
	@BlockerTodo
	public void migrate(Database outgoing, BiConsumer<Integer, String> listener) {
		// Todo: migrate tables and indexes
	}
}
