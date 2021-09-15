package com.re.paas.api.infra.database.document;

import java.util.List;

import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.infra.database.DatabaseAdapter;
import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.api.infra.database.model.BatchGetItemSpec;
import com.re.paas.api.infra.database.model.BatchGetItemResult;
import com.re.paas.api.infra.database.model.BatchWriteItemSpec;
import com.re.paas.api.infra.database.model.BatchWriteItemResult;
import com.re.paas.api.infra.database.textsearch.TextSearch;
import com.re.paas.api.runtime.SecureMethod;

public interface Database {

	public static Database get() {
		return (Database) DatabaseAdapter.getDelegate().getDatabase();
	}
	
	@SecureMethod
	boolean load(LoadPhase phase);
	
	Table getTable(String name);
	
	Table createTable(Class<? extends BaseTable> model);

	List<String> listTables();

	BatchGetItemResult batchGetItem(BatchGetItemSpec batchGetItemRequest);

	BatchWriteItemResult batchWriteItem(BatchWriteItemSpec batchWriteItemRequest);

	TextSearch getTextSearch();
	
}