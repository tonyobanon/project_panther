package com.re.paas.internal.infra.database.dynamodb;

import static com.re.paas.api.infra.database.document.xspec.QueryBuilder.S;
import static com.re.paas.api.infra.database.model.TableStatus.ACTIVE;
import static com.re.paas.internal.infra.database.dynamodb.classes.DynamoDBConstants.AttributeTypes.N;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.amazonaws.services.dynamodbv2.document.GetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateGlobalSecondaryIndexAction;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.GlobalSecondaryIndex;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.KeyAttribute;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QueryBuilder;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.api.infra.database.model.CapacityUnits;
import com.re.paas.api.infra.database.model.DeleteItemResult;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDescription;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.model.IndexStatus;
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
import com.re.paas.internal.infra.database.dynamodb.classes.DeleteItemEvent;
import com.re.paas.internal.infra.database.dynamodb.classes.GetItemEvent;
import com.re.paas.internal.infra.database.dynamodb.classes.Namespace;
import com.re.paas.internal.infra.database.dynamodb.classes.PutItemEvent;
import com.re.paas.internal.infra.database.dynamodb.classes.ReturnValue;
import com.re.paas.internal.infra.database.dynamodb.classes.Schemas;
import com.re.paas.internal.infra.database.dynamodb.classes.TablePrimaryKey;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.SchemaSpec;

public class TableImpl implements Table {

	private final com.amazonaws.services.dynamodbv2.document.Table awsTable;

	private final DatabaseImpl dbImpl;

	private TableDescription description;

	private static Map<String, Map<String, String>> attributes = new HashMap<>();
	private static Map<String, String> hashKey;
	private static Map<String, String> rangeKey;

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

	private static void loadSchema(String tableName) {

		DatabaseImpl dbImpl = (DatabaseImpl) Database.get();
		TableImpl schemaTable = (TableImpl) dbImpl.getTable0(SchemaSpec.TABLE_NAME);

		com.amazonaws.services.dynamodbv2.document.Item schema = schemaTable
				.getItem0(
						new QueryBuilder().buildForGetItem().setPrimaryKey(new PrimaryKey(SchemaSpec.PATH, tableName)))
				.getItem();

		Map<String, String> attributes = new HashMap<>();

		new JsonObject(schema.getString(SchemaSpec.ATTRIBUTES)).getMap().forEach((k, v) -> {
			attributes.put(k, (String) v);
		});

		TableImpl.attributes.put(tableName, attributes);

		TableImpl.hashKey.put(tableName, schema.getString(SchemaSpec.HASH_KEY));
		TableImpl.rangeKey.put(tableName, schema.getString(SchemaSpec.RANGE_KEY));
	}

	private static void ensureSchemaLoaded(String tableName) {

		// We don't want to use TableImpl.attributes because it can be populated by
		// refreshSchema(...) even before loadSchema() is called

		if (!TableImpl.hashKey.containsKey(tableName)) {
			loadSchema(tableName);
		}
	}

	static Map<String, String> getAttributes(String tableName) {
		ensureSchemaLoaded(tableName);
		return TableImpl.attributes.get(tableName);
	}

	static TablePrimaryKey getPrimaryKey(String tableName, Item item) {

		boolean foundHash = false;
		String hashKey = TableImpl.getHashKey(tableName);
		Object hashValue = null;

		boolean foundRange = false;
		String rangeKey = TableImpl.getRangeKey(tableName);
		Object rangeValue = null;

		for (Entry<String, Object> attr : item.attributes()) {

			if (hashKey.equals(attr.getKey())) {
				hashValue = attr.getValue();
				foundHash = true;
			} else if (rangeKey != null && rangeKey.equals(attr.getKey())) {
				rangeValue = attr.getValue();
				foundRange = true;
			}

			if (foundHash && foundRange) {
				break;
			}
		}

		return new TablePrimaryKey(tableName, KeyAttribute.toCollection(hashKey, hashValue, rangeKey, rangeValue));
	}

	static String getHashKey(String tableName) {
		ensureSchemaLoaded(tableName);
		return TableImpl.hashKey.get(tableName);
	}

	static String getRangeKey(String tableName) {
		ensureSchemaLoaded(tableName);
		return TableImpl.rangeKey.get(tableName);
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
	public Item getItem(GetItemSpec spec) {

		ensureTableQueryable();

		GetItemOutcome a = getItem0(spec);
		Item i = a.getItem() != null ? Item.fromMap(a.getItem().asMap()) : null;

		CapacityProvisioner.consumeRead(namespace, a.getGetItemResult().getConsumedCapacity());

		BaseEvent.getDelegate()
				.dispatch(new GetItemEvent(new TablePrimaryKey(name(), spec.getPrimaryKey().getComponents()), i), true);

		return i;
	}

	GetItemOutcome getItem0(GetItemSpec spec) {
		return awsTable.getItemOutcome(Marshallers.toGetItemSpec(spec));
	}

	@Override
	public PutItemResult putItem(PutItemSpec spec) {

		ensureTableQueryable();

		com.amazonaws.services.dynamodbv2.model.PutItemResult a = putItem0(spec, ReturnValue.ALL_OLD);

		Map<String, Object> attributes = Marshallers.toObjectMap(a.getAttributes());

		CapacityProvisioner.consumeWrite(namespace, a.getConsumedCapacity());

		// Note: To know if this is a new item: attributes != null
		BaseEvent.getDelegate().dispatch(new PutItemEvent(getPrimaryKey(name(), spec.getItem()), spec.getItem()), true);

		return new PutItemResult().setAttributes(attributes);
	}
	

	com.amazonaws.services.dynamodbv2.model.PutItemResult putItem0(PutItemSpec spec, ReturnValue returnValue) {
		return awsTable.putItem(Marshallers.toPutItemSpec(spec, returnValue)).getPutItemResult();
	}
	

	@Override
	public UpdateItemResult updateItem(UpdateItemSpec spec) {

		ensureTableQueryable();

		com.amazonaws.services.dynamodbv2.model.UpdateItemResult a = updateItem0(spec, ReturnValue.ALL_NEW);

		Map<String, Object> attributes = Marshallers.toObjectMap(a.getAttributes());

		CapacityProvisioner.consumeWrite(namespace, a.getConsumedCapacity());

		// Note: We currently do not have a way to know if this is was actually a new
		// item, or an update to an existing item
		BaseEvent.getDelegate().dispatch(new PutItemEvent(
				new TablePrimaryKey(name(), spec.getPrimaryKey().getComponents()), Item.fromMap(attributes)), true);

		return new UpdateItemResult().setAttributes(attributes);
	}

	com.amazonaws.services.dynamodbv2.model.UpdateItemResult updateItem0(UpdateItemSpec spec, ReturnValue returnValue) {
		return awsTable.updateItem(Marshallers.toUpdateItemSpec(spec, returnValue)).getUpdateItemResult();
	}

	@Override
	public DeleteItemResult deleteItem(DeleteItemSpec spec) {

		ensureTableQueryable();

		com.amazonaws.services.dynamodbv2.model.DeleteItemResult a = deleteItem0(spec, ReturnValue.ALL_OLD);

		CapacityProvisioner.consumeWrite(namespace, a.getConsumedCapacity());

		BaseEvent.getDelegate()
				.dispatch(new DeleteItemEvent(new TablePrimaryKey(name(), spec.getPrimaryKey().getComponents())), true);

		return new DeleteItemResult().setAttributes(Marshallers.toObjectMap(a.getAttributes()));
	}

	com.amazonaws.services.dynamodbv2.model.DeleteItemResult deleteItem0(DeleteItemSpec spec, ReturnValue returnValue) {
		return awsTable.deleteItem(Marshallers.toDeleteItemSpec(spec, returnValue)).getDeleteItemResult();
	}

	@Override
	public QueryResult query(QuerySpec spec) {
		ensureTableQueryable();

		com.amazonaws.services.dynamodbv2.model.QueryResult a = query0(spec);

		CapacityProvisioner.consumeRead(namespace, a.getConsumedCapacity());

		return Marshallers.fromQueryResult(a);
	}

	com.amazonaws.services.dynamodbv2.model.QueryResult query0(QuerySpec spec) {
		return awsTable.query(Marshallers.toQuerySpec(spec)).getLastLowLevelResult().getQueryResult();
	}

	@Override
	public ScanResult scan(ScanSpec spec) {
		ensureTableQueryable();

		ItemCollection<ScanOutcome> a = awsTable.scan(Marshallers.toScanSpec(spec));
		com.amazonaws.services.dynamodbv2.model.ScanResult b = a.getLastLowLevelResult().getScanResult();

		CapacityProvisioner.consumeRead(namespace, b.getConsumedCapacity());

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

		Map<String, String> attributes = Schemas.generate(model);

		// We need to verify the keys for the table and all indexes are still present
		// in the model

		verifySchema(attributes, this.description.getKeySchema());

		this.description.getLocalSecondaryIndexes().forEach(lsi -> {
			verifySchema(attributes, lsi.getKeySchema());
		});

		this.description.getGlobalSecondaryIndexes().forEach(gsi -> {
			verifyGsiSchema(attributes, gsi);
		});

		TableImpl schemaTable = (TableImpl) dbImpl.getTable0(SchemaSpec.TABLE_NAME);

		schemaTable.updateItem0(
				new QueryBuilder().addUpdate(S(SchemaSpec.ATTRIBUTES).set(JsonParser.get().toString(attributes)))
						.withCondition(S(SchemaSpec.PATH).eq(name())).buildForUpdate(),
				ReturnValue.NONE);

		TableImpl.attributes.put(name(), attributes);
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
			// indexName is a LSI, this exception will also be thrown, because <desc> will
			// be null
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

		loadSchema(name());

		Map<String, String> attributes = getAttributes(name());

		verifyGsiSchema(attributes, definition);

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
				ScalarAttributeType.fromValue(attributes.get(hashKey)));

		AttributeDefinition rangeKey0 = rangeKey != null
				? new AttributeDefinition(rangeKey, ScalarAttributeType.fromValue(attributes.get(rangeKey)))
				: null;

		if (rangeKey0 != null) {
			awsTable.createGSI(req, hashKey0, rangeKey0);
		} else {
			awsTable.createGSI(req, hashKey0);
		}

		return (GlobalSecondaryIndex) getIndex(definition.getIndexName());
	}

	@BlockerTodo
	@Override
	public void deleteGSI(String indexName) {

		describe();

		ensureTableActive();

		ensureIndexExists(indexName);

		ensureIndexActive(indexName);

		GlobalSecondaryIndexDescription desc = getGsiDescription(indexName);

		if (desc.forTextSearch()) {

			// Todo: Create procedure for deleting text-search GSIs while table in service
			throw new RuntimeException("Unable to delete text-search GSI while table in service");
		}

		awsTable.getIndex(indexName).deleteGSI();

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
