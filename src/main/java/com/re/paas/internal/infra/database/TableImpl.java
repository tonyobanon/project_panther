package com.re.paas.internal.infra.database;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;
import static com.re.paas.api.infra.database.model.TableStatus.ACTIVE;
import static com.re.paas.internal.infra.database.DynamoDBConstants.AttributeTypes.N;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateGlobalSecondaryIndexAction;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.infra.database.Namespace;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.GlobalSecondaryIndex;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.api.infra.database.model.CapacityUnits;
import com.re.paas.api.infra.database.model.DeleteItemResult;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDescription;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.IndexStatus;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.model.KeySchemaElement;
import com.re.paas.api.infra.database.model.PutItemResult;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.TableStatus;
import com.re.paas.api.infra.database.model.UpdateItemResult;
import com.re.paas.api.infra.database.model.exceptions.IndexAlreadyExistsException;
import com.re.paas.api.infra.database.model.exceptions.IndexNotActiveException;
import com.re.paas.api.infra.database.model.exceptions.IndexNotFoundException;
import com.re.paas.api.infra.database.model.exceptions.TableNotActiveException;
import com.re.paas.api.infra.database.model.exceptions.TableNotFoundException;
import com.re.paas.api.infra.database.model.exceptions.TableNotReadyException;
import com.re.paas.api.infra.database.model.exceptions.UnknownAttributeTypeException;
import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.infra.database.tables.attributes.SchemaSpec;

public class TableImpl implements Table {

	private final com.amazonaws.services.dynamodbv2.document.Table awsTable;

	private final DatabaseImpl dbImpl;

	private TableDescription description;

	private Map<String, String> schema = new HashMap<String, String>();

	private List<String> localIndexes;

	private final Namespace namespace;

	TableImpl(DatabaseImpl db, com.amazonaws.services.dynamodbv2.document.Table awsTable) {
		this.dbImpl = db;
		this.awsTable = awsTable;
		
		this.namespace = Namespace.from(name());

		loadLocalIndexes();
	}

	@Override
	public Database getDatabase() {
		return dbImpl;
	}

	@Override
	public String name() {
		return awsTable.getTableName();
	}

	private void loadSchema() {

		String schemaString = dbImpl.getTable0(SchemaSpec.TABLE_NAME).getItem(new PrimaryKey(SchemaSpec.PATH, name()))
				.getString(SchemaSpec.TYPE);

		new JsonObject(schemaString).getMap().forEach((k, v) -> {
			schema.put(k, (String) v);
		});
	}

	@Override
	public TableDescription describe(boolean loadTextSearchInfo) {

		com.amazonaws.services.dynamodbv2.model.TableDescription a;

		try {
			a = this.awsTable.describe();

		} catch (ResourceNotFoundException ex) {
			throw new TableNotFoundException(name());
		}

		// Note: Marshallers.fromTableDescription(...) will make an extra call to the
		// IndexPartitionTable to check if this is a tsb index, while populating gsi
		// data

		TableDescription b = Marshallers.fromTableDescription(dbImpl, a, loadTextSearchInfo);

		this.description = b;

		return this.description;
	}

	private void loadLocalIndexes() {

		if (localIndexes != null) {
			return;
		}

		localIndexes = new ArrayList<String>();

		if (this.description == null) {
			describe();
		}

		this.description.getLocalSecondaryIndexes().forEach(lsi -> {
			localIndexes.add(lsi.getIndexName());
		});
	}

	@Override
	public Index getIndex(String indexName) {
		ensureIndexExists(indexName);

		return getIndex0(indexName, localIndexes.contains(indexName) ? Type.LSI :

		// Note: If this GSI was recently deleted on another node in the cluster,
		// we will likely encounter an error when we try to use this index
				Type.GSI);
	}

	private boolean isTableQueryable(boolean waitIfCreating) {

		if (this.description == null) {
			describe();
		}

		// Note: Due to the time-sensitive nature of this method invocation, we are
		// unable to call describe() on the table every time to get the latest info,
		// so it is possible for the table info retrieved to be stale.

		TableStatus status = this.description.getTableStatus();

		switch (status) {

		case UPDATING:
		case ACTIVE:
			return true;

		case CREATING:

			if (waitIfCreating) {

				waitForActive();
				return true;
			}

		case DELETING:
		default:
			return false;
		}
	}

	private void ensureTableQueryable() {

		if (!isTableQueryable(true)) {
			throw new TableNotReadyException(name());
		}
	}

	private boolean doesIndexExists(String indexName) {

		ensureTableQueryable();

		return this.description.getIndexNames().contains(indexName);
	}

	private void ensureIndexExists(String indexName) {

		if (!doesIndexExists(indexName)) {

			// We did not find the index, but it's possible that createGSI(...) was called
			// on another node in the cluster, so we need to re-describe this table
			describe();

			if (!doesIndexExists(indexName)) {
				throw new IndexNotFoundException(indexName);
			}
		}

		return;
	}

	private Index getIndex0(String indexName, Type type) {

		Index index = new IndexImpl(this, awsTable.getIndex(indexName), type);

		if (type == Type.GSI) {
			index = new GlobalSecondaryIndexImpl((IndexImpl) index);
		}

		return index;
	}

	TableDescription getDescription() {
		return description;
	}

	GlobalSecondaryIndexDescription getGsiDescription(String indexName) {

		Stream<GlobalSecondaryIndexDescription> stream = getDescription().getGlobalSecondaryIndexes().stream()
				.filter(gsi -> gsi.getIndexName().equals(indexName));

		try {
			return stream.findFirst().get();
		} catch (NoSuchElementException ex) {
			return null;
		}
	}

	@Override
	public PutItemResult putItem(PutItemSpec spec) {
		ensureTableQueryable();

		// getAttributeModel().putValue(getTableName(), item);
		
		
		
		CapacityProvisioner.consumeRead(namespace, b.getConsumedCapacity().getReadCapacityUnits());
		
		return null;
	}

	@Override
	public Item getItem(GetItemSpec spec) {
		ensureTableQueryable();

		
		return null;
	}

	@Override
	public UpdateItemResult updateItem(UpdateItemSpec updateItemSpec) {
		ensureTableQueryable();

		// getAttributeModel().updateValue(getTableName(),
		// outcome.getUpdateItemResult().getAttributes(),
		// updateItemSpec.getKeyComponents());
		return null;
	}

	@Override
	public DeleteItemResult deleteItem(DeleteItemSpec spec) {
		ensureTableQueryable();

		// getAttributeModel().deleteValue(getTableName(), spec.getKeyComponents());
		return null;
	}

	@Override
	public QueryResult query(QuerySpec spec) {
		ensureTableQueryable();

		ItemCollection<QueryOutcome> a = awsTable.query(Marshallers.toQuerySpec(spec));
		com.amazonaws.services.dynamodbv2.model.QueryResult b = a.getLastLowLevelResult().getQueryResult();

		CapacityProvisioner.consumeRead(namespace, b.getConsumedCapacity().getReadCapacityUnits());
		
		return Marshallers.fromQueryResult(b);
	}

	@Override
	public ScanResult scan(ScanSpec spec) {
		ensureTableQueryable();

		ItemCollection<ScanOutcome> a = awsTable.scan(Marshallers.toScanSpec(spec));
		com.amazonaws.services.dynamodbv2.model.ScanResult b = a.getLastLowLevelResult().getScanResult();

		CapacityProvisioner.consumeRead(namespace, b.getConsumedCapacity().getReadCapacityUnits());
		
		return Marshallers.fromScanResult(b);
	}

	private static void verifySchema(Map<String, String> schema, Collection<KeySchemaElement> keySchema) {
		keySchema.forEach(s -> {
			TableUtils.getScalarAttribute(schema, s.getAttributeName());
		});
	}

	private static void verifyGsiSchema(Map<String, String> schema, GlobalSecondaryIndexDefinition gsi) {
		verifySchema(schema, gsi.getKeySchema());

		if (gsi.forTextSearch()) {

			// We want the hash key to be a number
			String hashKey = KeySchemaElement.getHashKey(gsi.getKeySchema());
			String type = schema.get(hashKey);

			if (!type.equals(N)) {
				throw new UnknownAttributeTypeException(hashKey, type);
			}
		}
	}

	@Override
	public void refreshSchema(Class<? extends BaseTable> model) {

		// We always need to describe, especially because we need to have a view of all
		// the global secondary indexes
		describe();

		Map<String, String> schema = Schemas.generate(model);

		// We need to verify the keys for the table and all indexes are still present
		// in the model

		verifySchema(schema, this.description.getKeySchema());

		this.description.getLocalSecondaryIndexes().forEach(lsi -> {
			verifySchema(schema, lsi.getKeySchema());
		});

		this.description.getGlobalSecondaryIndexes().forEach(gsi -> {
			verifyGsiSchema(schema, gsi);
		});

		dbImpl.getTable0(SchemaSpec.TABLE_NAME).updateItem(
				new ExpressionSpecBuilder().addUpdate(S(SchemaSpec.TYPE).set(JsonParser.get().toString(schema)))
						.withCondition(S(SchemaSpec.PATH).eq(name())).buildForUpdate());

		this.schema = schema;
	}

	private void ensureTableActive() {

		if (this.description.getTableStatus() != ACTIVE) {

			// Note: we would normally call describe, and check the condition again before
			// failing. But for this method, all callers already call describe() before
			// calling it, hence it's not needed in this scenario.

			throw new TableNotActiveException(name());
		}
	}

	private void ensureIndexActive(String indexName) {

		GlobalSecondaryIndexDescription desc = getGsiDescription(indexName);

		if (desc == null) {
			
			// We expect indexName to be a GSI, because LSIs are always active. Hence, if
			// indexName is a LSI, this exception will also be thrown, because <desc> will be null
			throw new IndexNotFoundException(indexName);
		}

		IndexStatus status = desc.getIndexStatus();

		if (status != IndexStatus.ACTIVE) {

			// Note: we would normally call describe, and check the condition again before
			// failing. But for this method, all callers already call describe() before
			// calling it, hence it's not needed in this scenario.

			throw new IndexNotActiveException(indexName);
		}
	}

	@Todo
	@Override
	public GlobalSecondaryIndex createGSI(GlobalSecondaryIndexDefinition definition) {

		if (definition.forTextSearch()) {
			// Todo: Create procedure for creating text-search GSIs after table creation
			throw new RuntimeException("Unable to create text-search GSI after table creation");
		}
		
		loadSchema();

		verifyGsiSchema(this.schema, definition);

		describe();

		if (doesIndexExists(definition.getIndexName())) {
			throw new IndexAlreadyExistsException(definition.getIndexName());
		}

		ensureTableActive();

		CapacityUnits capacity = CapacityProvisioner.createGsiCapacity(name(), definition);

		CreateGlobalSecondaryIndexAction req = Marshallers.toCreateGlobalSecondaryIndexAction(definition, capacity);

		String hashKey = KeySchemaElement.getHashKey(definition.getKeySchema());
		String rangeKey = KeySchemaElement.getRangeKey(definition.getKeySchema());

		AttributeDefinition hashKey0 = new AttributeDefinition(hashKey,
				ScalarAttributeType.fromValue(schema.get(hashKey)));
		
		AttributeDefinition rangeKey0 = rangeKey != null
				? new AttributeDefinition(rangeKey, ScalarAttributeType.fromValue(schema.get(rangeKey)))
				: null;

		if (rangeKey0 != null) {
			awsTable.createGSI(req, hashKey0, rangeKey0);
		} else {
			awsTable.createGSI(req, hashKey0);
		}

		return (GlobalSecondaryIndex) getIndex(definition.getIndexName());
	}

	@Todo
	void deleteGSI(IndexDescriptor index) {

		describe();

		ensureTableActive();

		ensureIndexExists(index.getIndexName());

		ensureIndexActive(index.getIndexName());

		GlobalSecondaryIndexDescription desc = getGsiDescription(index.getIndexName());

		if (desc.forTextSearch()) {
			// Todo: Create procedure for deleting text-search GSIs while table in service
			throw new RuntimeException("Unable to delete text-search GSI while table in service");
		}
		
		awsTable.getIndex(index.getIndexName()).deleteGSI();
		
		desc.setIndexStatus(IndexStatus.DELETING);
	}

	private void waitForActive0() {
		try {
			awsTable.waitForActive();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public Table waitForActive() {

		if (this.description == null) {
			describe();
		}

		TableStatus status = this.description.getTableStatus();

		if (status == TableStatus.CREATING) {

			waitForActive0();

			this.description.setTableStatus(TableStatus.ACTIVE);
		}

		return this;
	}

	@Override
	public Table delete() {

		describe();

		ensureTableActive();

		awsTable.delete();

		this.description.setTableStatus(TableStatus.DELETING);
		return this;
	}

	private void waitForDelete0() {
		try {
			awsTable.waitForDelete();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public Table waitForDelete() {

		if (this.description == null) {
			describe();
		}
		
		TableStatus status = this.description.getTableStatus();

		if (status == TableStatus.DELETING) {
			waitForDelete0();
		}

		return this;
	}
}
