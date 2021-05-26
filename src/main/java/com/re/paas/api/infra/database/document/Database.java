package com.re.paas.api.infra.database.document;

import java.util.List;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.DatabaseAdapter;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.BatchGetItemResult;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.TableDefinition;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.TableUpdate;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.api.infra.database.textsearch.TextSearch;
import com.re.paas.internal.infra.database.ThrottlePolicy;

@BlockerTodo
public interface Database {

	public static Database get() {
		return (Database) DatabaseAdapter.getDelegate().getDatabase();
	}
	
	/**
	 * This is used by the Database implementation to load artifacts from the db into the system.
	 * It is called each time the database adapter loads, typically during platform startup.
	 */
	void load();

	Database lean();
	
	Table getTable(String name);
	
	default Table getTable(Class<? extends BaseTable> clazz) {
		return getTable(clazz.getSimpleName());
	}
	
	ThrottlePolicy throttlePolicy();

	Table createTable(TableDefinition request);

	Table deleteTable(String name);

	public List<String> listTables();

	TableDescription updateTable(TableUpdate update);

	BatchGetItemResult batchGetItem(BatchGetItemRequest batchGetItemRequest);

	void batchWriteItem(BatchWriteItemRequest batchWriteItemRequest);

	TextSearch getTextSearch();
}