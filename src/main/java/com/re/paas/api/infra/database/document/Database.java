package com.re.paas.api.infra.database.document;

import java.util.List;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.DatabaseAdapter;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.BatchGetItemResult;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.BatchWriteItemResult;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.api.infra.database.textsearch.TextSearch;

@BlockerTodo
public interface Database {

	public static Database get() {
		return (Database) DatabaseAdapter.getDelegate().getDatabase();
	}
	
	Table getTable(String name);
	
	Table createTable(Class<? extends BaseTable> clazz);

	void deleteTable(String name);

	public List<String> listTables();

	BatchGetItemResult batchGetItem(BatchGetItemRequest batchGetItemRequest);

	BatchWriteItemResult batchWriteItem(BatchWriteItemRequest batchWriteItemRequest);

	TextSearch getTextSearch();
}