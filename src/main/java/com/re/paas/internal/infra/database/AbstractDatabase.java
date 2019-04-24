package com.re.paas.internal.infra.database;

import java.util.List;

import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.BatchGetItemResult;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.TableDefinition;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.TableUpdate;
import com.re.paas.api.infra.database.textsearch.TextSearch;

public abstract class AbstractDatabase implements Database {

	@Override
	public void load() {
		
		
		
		
		// Register singletons
		
		
		
		// Based on the throttlePolicy, call QM.loadIndexedGSIs(...)
		
	}
	
	@Override
	public Database lean() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public abstract ThrottlePolicy throttlePolicy();

	@Override
	public Table getTable(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableDescription createTable(TableDefinition request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableDescription deleteTable(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listTables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableDescription updateTable(TableUpdate update) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchGetItemResult batchGetItem(BatchGetItemRequest batchGetItemRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void batchWriteItem(BatchWriteItemRequest batchWriteItemRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	public TextSearch getTextSearch() {
		// TODO Auto-generated method stub
		return null;
	}

}
