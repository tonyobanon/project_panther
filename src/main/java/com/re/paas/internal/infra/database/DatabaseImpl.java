package com.re.paas.internal.infra.database;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.re.paas.api.infra.database.Namespace;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.utils.TableUtils;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.BatchGetItemResult;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.BatchWriteItemResult;
import com.re.paas.api.infra.database.model.CapacityUnits;
import com.re.paas.api.infra.database.model.LocalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.TableDefinition;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.api.infra.database.textsearch.TextSearch;

public class DatabaseImpl implements Database {

	private final String accessKey;
	private final String secretKey;
	private final String region;

	private final DynamoDB awsDatabase;

	DatabaseImpl(String accessKey, String secretKey, String region) {

		this.accessKey = accessKey;
		this.secretKey = secretKey;

		this.region = region;

		AmazonDynamoDBAsync dynamodbAsync = AmazonDynamoDBAsyncClientBuilder.standard()
				.withCredentials(
						new AWSStaticCredentialsProvider(new BasicAWSCredentials(this.accessKey, this.secretKey)))
				.withRegion(Regions.fromName(region)).build();

		this.awsDatabase = new DynamoDB(dynamodbAsync);
	}

	@Override
	public Table getTable(String name) {

		return null;
	}

	@Override
	public Table createTable(Class<? extends BaseTable> clazz) {

		TableDefinition def = TableUtils.toTableDefinition(clazz);

		CapacityUnits capacity = CapacityProvisioner.defaultTableCapacity();
		CapacityProvisioner.create(new Namespace(def.getTableName()), true);

				
		CreateTableRequest req = new CreateTableRequest(
				def.getAttributeDefinitions().stream().map(Marshallers::toAttributeDefinition)
						.collect(Collectors.toList()),
				def.getTableName(),
				def.getKeySchema().stream().map(Marshallers::toKeySchemaElement).collect(Collectors.toList()),
				new ProvisionedThroughput(capacity.getReadCapacityUnits().longValue(),
						capacity.getWriteCapacityUnits().longValue()))
				
				// Add LSIs
				.withLocalSecondaryIndexes(
					def.getLocalSecondaryIndexes()
					.stream()
					.map(a -> {
						return new LocalSecondaryIndex()
								.withIndexName(a.getIndexName())
								.withKeySchema(a.getKeySchema().stream().map(Marshallers::toKeySchemaElement).collect(Collectors.toList()))
								.withProjection(new Projection().withProjectionType(a.getProjection().getProjectionType().toString()).withNonKeyAttributes(a.getProjection().getNonKeyAttributes()));

					})
					.collect(Collectors.toList())
				);
		
			

		
		return null;

	}

	@Override
	public void deleteTable(String name) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> listTables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchGetItemResult batchGetItem(BatchGetItemRequest batchGetItemRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BatchWriteItemResult batchWriteItem(BatchWriteItemRequest batchWriteItemRequest) {

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

}
