package com.re.paas.internal.infra.database.dynamodb.qopt.tools;

import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.BatchWriteItemSpec;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.kylantis.eaa.core.base.StorageServiceFactory;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.internal.infra.database.dynamodb.AttributeModel;

public class DynamoDBAdapter extends DynamoDB {

	private AttributeModel attributeModel;
	private final AmazonDynamoDB client;
	
	public DynamoDBAdapter(AmazonDynamoDB client) {
		super(client);
		this.client = client;
		this.attributeModel = new AttributeModelImpl();
	}

	public DynamoDBAdapter(Regions regionEnum) {
		super(regionEnum);
		client = StorageServiceFactory.getDatabase();
		this.attributeModel = new AttributeModelImpl();
	}
	
	private AttributeModel getAttributeModel() {
		return attributeModel;
	}
	
	@Override
	@Todo("Filter items by removing all unprocessed write requests")
	public BatchWriteItemOutcome batchWriteItem(BatchWriteItemSpec spec) {
		
		
		//spec.getTableWriteItems()
		
		try {
			BatchWriteItemOutcome outcome = super.batchWriteItem(spec);
			
			for(TableWriteItems items : spec.getTableWriteItems()){
				
				if(items.getPrimaryKeysToDelete() != null){
					for(PrimaryKey key : items.getPrimaryKeysToDelete()){
						getAttributeModel().deleteValue(items.getTableName(), key.getComponents());
					}
				}
				
				if(items.getItemsToPut() != null){
					for(Item item : items.getItemsToPut()){
						getAttributeModel().putValue(items.getTableName(), item);
					}
				}
			}
			
			return outcome;
		} catch (RuntimeException e) {
			throw e;
		}
	}
	
	@Override
	public BatchWriteItemOutcome batchWriteItem(TableWriteItems... tableWriteItems) {		
		return batchWriteItem(new BatchWriteItemSpec().withTableWriteItems(tableWriteItems));
	}
	
	@BlockerTodo("Implement this")
	@Override
	public BatchWriteItemOutcome batchWriteItemUnprocessed(Map<String, List<WriteRequest>> unprocessedItems) {		
		return super.batchWriteItemUnprocessed(unprocessedItems);
	}
	
	@Override
	public TableAdapter getTable(String tableName) {
		//Table table = super.getTable(tableName);
		return new TableAdapter(client, tableName);
	}
	
	@Override
	public void shutdown() {
		super.shutdown();
		client.shutdown();
	}
}
