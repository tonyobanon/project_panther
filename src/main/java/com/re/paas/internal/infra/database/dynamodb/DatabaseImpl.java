package com.re.paas.internal.infra.database.dynamodb;

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
import com.re.paas.internal.infra.database.ThrottlePolicy;

public class DatabaseImpl implements Database {

	DatabaseImpl() {
		
	}
	
	@Override
	public ThrottlePolicy throttlePolicy() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Table getTable(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Table createTable(TableDefinition request) {
		return null;
	}

	@Override
	public Table deleteTable(String name) {
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
		
//		try {
//			BatchWriteItemOutcome outcome = super.batchWriteItem(spec);
//			
//			for(TableWriteItems items : spec.getTableWriteItems()){
//				
//				if(items.getPrimaryKeysToDelete() != null){
//					for(PrimaryKey key : items.getPrimaryKeysToDelete()){
//						getAttributeModel().deleteValue(items.getTableName(), key.getComponents());
//					}
//				}
//				
//				if(items.getItemsToPut() != null){
//					for(Item item : items.getItemsToPut()){
//						getAttributeModel().putValue(items.getTableName(), item);
//					}
//				}
//			}
//			
//			return outcome;
//		} catch (RuntimeException e) {
//			throw e;
//		}
	}

	@Override
	public TextSearch getTextSearch() {
		
		return null;
	}

	@Override
	public Database lean() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

}
