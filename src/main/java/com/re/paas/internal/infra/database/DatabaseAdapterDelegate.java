package com.re.paas.internal.infra.database;

import java.util.function.BiConsumer;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.AbstractDatabaseAdapterDelegate;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.internal.classes.ClasspathScanner;

@DelegateSpec(dependencies = SpiType.NODE_ROLE)
public class DatabaseAdapterDelegate extends AbstractDatabaseAdapterDelegate {

	private static Database database;

	@Override
	public Boolean load(LoadPhase phase) {

		if (phase == LoadPhase.PLATFORM_SETUP) {

			// Create platform tables
			Database db = getDatabase(true);

			new ClasspathScanner<>(BaseTable.class, ClassIdentityType.ASSIGNABLE_FROM).scanClasses().forEach(clazz -> {
				db.createTable(clazz);
			});
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

	@Override
	@BlockerTodo
	public void migrate(Database outgoing, BiConsumer<Integer, String> listener) {

		// Create tables

		// Create Indexes

		// Scan each table
	}
}
