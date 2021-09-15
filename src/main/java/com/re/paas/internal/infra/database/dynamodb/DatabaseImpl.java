package com.re.paas.internal.infra.database.dynamodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.re.paas.api.adapters.LoadPhase;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.QueryBuilder;
import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.api.infra.database.model.BatchGetItemResult;
import com.re.paas.api.infra.database.model.BatchGetItemSpec;
import com.re.paas.api.infra.database.model.BatchWriteItemResult;
import com.re.paas.api.infra.database.model.BatchWriteItemSpec;
import com.re.paas.api.infra.database.model.CapacityUnits;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.KeySchemaElement;
import com.re.paas.api.infra.database.model.TableDefinition;
import com.re.paas.api.infra.database.model.exceptions.TableAlreadyExistsException;
import com.re.paas.api.infra.database.model.exceptions.TableNotFoundException;
import com.re.paas.api.infra.database.textsearch.TextSearch;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.infra.database.dynamodb.classes.ReturnValue;
import com.re.paas.internal.infra.database.dynamodb.classes.Schemas;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.SchemaSpec;

public class DatabaseImpl implements Database {

	private final DynamoDB awsDatabase;

	private List<String> allTables;
	private List<String> userTables;

	private Map<String, TableImpl> tablesMap = new HashMap<String, TableImpl>();

	DatabaseImpl(String accessKey, String secretKey, String region) {

		AmazonDynamoDBAsync dynamodbAsync = AmazonDynamoDBAsyncClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
				.withRegion(Regions.fromName(region)).build();

		this.awsDatabase = new DynamoDB(dynamodbAsync);
	}

	@Override
	public boolean load(LoadPhase phase) {

		switch (phase) {

		case MIGRATE:
			break;

		case PLATFORM_SETUP:

			// Create platform tables
			new ClasspathScanner<>(BaseTable.class, ClassIdentityType.ASSIGNABLE_FROM).scanClasses().forEach(clazz -> {
				createTable(clazz);
			});
			break;

		case START:

			// Load table names
			listTables();

			// Load data needed for text search orchestration
			getQueryModel().load();

			break;
		}

		return true;
	}
	
	private QueryInterfaceImpl getQueryModel() {
		return (QueryInterfaceImpl) getTextSearch().getQueryInterface();
	}

	@Override
	public Table getTable(String name) {

		if (!doesUserTableExist(name)) {
			throw new TableNotFoundException(name);
		}

		return getTable0(name);
	}

	Table getTable0(String name) {
		TableImpl t = tablesMap.get(name);

		if (t == null) {
			t = new TableImpl(this, awsDatabase.getTable(name));
			tablesMap.put(name, t);
		}

		return t;
	}

	private Boolean doesUserTableExist(String tableName) {
		boolean b = this.userTables.contains(tableName);

		if (!b) {
			// The table may have been created on a different node
			listTables();
			b = this.userTables.contains(tableName);
		}

		return b;
	}

	@Override
	public Table createTable(Class<? extends BaseTable> model) {
		return createTable(model, true);
	}

	Table createTable(Class<? extends BaseTable> model, Boolean userDefined) {

		listTables();

		Map<String, String> schema = Schemas.generate(model);

		TableDefinition def = TableUtils.toTableDefinition(schema, model);

		String tableName = def.getTableName();

		if (allTables.contains(tableName)) {
			throw new TableAlreadyExistsException(tableName);
		}

		List<KeySchemaElement> tableKeySchema = def.getKeySchema();

		CapacityUnits tableCapacity = CapacityProvisioner.createTableCapacity(tableName);

		CreateTableRequest req = new CreateTableRequest(
				// Attribute Definitions
				def.getAttributeDefinitions().stream().map(Marshallers::toAttributeDefinition)
						.collect(Collectors.toList()),
				// Table name
				tableName,
				// Key Schema
				tableKeySchema.stream().map(Marshallers::toKeySchemaElement).collect(Collectors.toList()),
				// Provisioned Throughput
				new ProvisionedThroughput(tableCapacity.getReadCapacityUnits().longValue(),
						tableCapacity.getWriteCapacityUnits().longValue()))

								// Add LSIs
								.withLocalSecondaryIndexes(def.getLocalSecondaryIndexes().stream().map(a -> {
									return new LocalSecondaryIndex().withIndexName(a.getIndexName())
											.withKeySchema(a.getKeySchema().stream()
													.map(Marshallers::toKeySchemaElement).collect(Collectors.toList()))
											.withProjection(new Projection()
													.withProjectionType(
															a.getProjection().getProjectionType().toString())
													.withNonKeyAttributes(a.getProjection().getNonKeyAttributes()));

								}).collect(Collectors.toList()))

								// Add GSIs
								.withGlobalSecondaryIndexes(def.getGlobalSecondaryIndexes().stream().map(gsi -> {

									CapacityUnits gsiCapacity = CapacityProvisioner.createGsiCapacity(tableName, gsi);

									if (gsi.forTextSearch()) {

										// Note: this is adding to the index even before we actually create the tables

										getQueryModel().addIndex(
												new IndexDescriptor(tableName, gsi.getIndexName()),
												gsi.getProjection().getNonKeyAttributes(), gsi.getQueryType(),
												KeySchemaElement.getHashKey(gsi.getKeySchema()),
												KeySchemaElement.getRangeKey(gsi.getKeySchema()),
												gsiCapacity.getReadCapacityUnits());

									}

									return Marshallers.toGlobalSecondaryIndex(gsi, gsiCapacity);

								}).collect(Collectors.toList()));

		// Create Table on AWS
		awsDatabase.createTable(req);

		allTables.add(req.getTableName());

		if (userDefined) {
			userTables.add(req.getTableName());

			// Save schema information

			TableImpl schemaTable = (TableImpl) getTable0(SchemaSpec.TABLE_NAME);

			schemaTable
					.putItem0(
							new QueryBuilder().buildForPut()
									.withItem(new Item().withString(SchemaSpec.PATH, req.getTableName())
											.withString(SchemaSpec.ATTRIBUTES, JsonParser.get().toString(schema))
											.withString(SchemaSpec.HASH_KEY, KeySchemaElement.getHashKey(tableKeySchema))
											.withString(SchemaSpec.RANGE_KEY, KeySchemaElement.getRangeKey(tableKeySchema))
											),
							ReturnValue.NONE);
		}

		return getTable0(req.getTableName());
	}

	@Override
	public List<String> listTables() {
		List<String> result = new ArrayList<>();

		awsDatabase.listTables().forEach(t -> {
			result.add(t.getTableName());
		});

		this.allTables = result;
		this.userTables = getUserDefindedTables(result);

		return this.allTables;
	}

	private List<String> getUserDefindedTables(List<String> tableNames) {

		List<TableKeysAndAttributes> keys = new ArrayList<>();

		tableNames.forEach(name -> {
			keys.add(new TableKeysAndAttributes(SchemaSpec.TABLE_NAME).addHashOnlyPrimaryKey(SchemaSpec.PATH, name)
					.withProjectionExpression(ProjectionType.KEYS_ONLY.toString()));
		});

		TableKeysAndAttributes[] keysArray = keys.toArray(new TableKeysAndAttributes[keys.size()]);

		return awsDatabase
				.batchGetItem(new com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec()
						.withTableKeyAndAttributes(keysArray))
				.getTableItems().get(SchemaSpec.TABLE_NAME).stream().map(i -> i.getString(SchemaSpec.PATH))
				.collect(Collectors.toList());
	}

	@Override
	public BatchGetItemResult batchGetItem(BatchGetItemSpec spec) {

		com.amazonaws.services.dynamodbv2.model.BatchGetItemResult a = awsDatabase
				.batchGetItem(Marshallers.toBatchGetItemSpec(spec)).getBatchGetItemResult();


		return null;
	}
	
	private com.amazonaws.services.dynamodbv2.model.BatchGetItemResult batchGetItem0(BatchGetItemSpec spec) {
		return awsDatabase.batchGetItem(Marshallers.toBatchGetItemSpec(spec)).getBatchGetItemResult();
	}

	@Override
	@BlockerTodo("Follow up with: https://console.aws.amazon.com/support/home#/case/?displayId=8889119611&language=en")
	public BatchWriteItemResult batchWriteItem(BatchWriteItemSpec spec) {

		com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult a = awsDatabase
				.batchWriteItem(Marshallers.toBatchWriteItemSpec(spec)).getBatchWriteItemResult();

		return null;
	}
	
	com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult batchWriteItem0(BatchWriteItemSpec spec) {
		return awsDatabase.batchWriteItem(Marshallers.toBatchWriteItemSpec(spec)).getBatchWriteItemResult();
	}

	@Override
	public TextSearch getTextSearch() {
		return new TextSearchImpl();
	}

}
