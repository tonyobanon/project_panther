package com.re.paas.internal.infra.database;


import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.re.paas.api.infra.database.Namespace;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.utils.ItemUtils;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.CapacityUnits;
import com.re.paas.api.infra.database.model.DeleteItemResult;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.KeyType;
import com.re.paas.api.infra.database.model.PutItemResult;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.UpdateItemResult;
import com.re.paas.api.infra.database.model.UpdateTableSpec;

public class TableImpl implements Table {

	private final com.amazonaws.services.dynamodbv2.document.Table awsTable;

	private final Database db;
	private TableDescription description;
	private final Namespace namespace;

	TableImpl(Database db, com.amazonaws.services.dynamodbv2.document.Table awsTable) {
		this.db = db;
		this.awsTable = awsTable;
		this.namespace = new Namespace(awsTable.getTableName());
	}

	@Override
	public Database getDatabase() {
		return db;
	}

	@Override
	public String name() {
		return awsTable.getTableName();
	}

	@Override
	public TableDescription describe() {

		com.amazonaws.services.dynamodbv2.model.TableDescription a = this.awsTable.describe();
		
		// Note: Marshallers.fromTableDescription(...) will make an extra call to the IndexPartitionTable
		// to check if this is a tsb index, while populating gsi data
		
		TableDescription b = Marshallers.fromTableDescription(db, a);

		this.description = b;

		return this.description;
	}

	@Override
	public Index getIndex(String indexName) {
		return new IndexImpl(this, awsTable.getIndex(indexName));
	}

	@Override
	public PutItemResult putItem(PutItemSpec spec) {
		// getAttributeModel().putValue(getTableName(), item);
		return null;
	}

	@Override
	public Item getItem(GetItemSpec spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateItemResult updateItem(UpdateItemSpec updateItemSpec) {
		// getAttributeModel().updateValue(getTableName(),
		// outcome.getUpdateItemResult().getAttributes(),
		// updateItemSpec.getKeyComponents());
		return null;
	}

	@Override
	public DeleteItemResult deleteItem(DeleteItemSpec spec) {
		// getAttributeModel().deleteValue(getTableName(), spec.getKeyComponents());
		return null;
	}
	
	@Override
	public QueryResult query(QuerySpec spec) {

		ItemCollection<QueryOutcome> a = awsTable.query(Marshallers.toQuerySpec(spec));
		com.amazonaws.services.dynamodbv2.model.QueryResult b = a.getLastLowLevelResult().getQueryResult();

		CapacityProvisioner.consume(namespace, Marshallers.fromConsumedCapacity(b.getConsumedCapacity()));
		
		return Marshallers.fromQueryResult(b);
	}

	@Override
	public ScanResult scan(ScanSpec spec) {
		
		ItemCollection<ScanOutcome> a = awsTable.scan(Marshallers.toScanSpec(spec));
		com.amazonaws.services.dynamodbv2.model.ScanResult b = a.getLastLowLevelResult().getScanResult();

		CapacityProvisioner.consume(namespace, Marshallers.fromConsumedCapacity(b.getConsumedCapacity()));
		
		return Marshallers.fromScanResult(b);
	}

	@Override
	public Index createGSI(GlobalSecondaryIndexDefinition definition) {

		CapacityUnits capacity = CapacityProvisioner.defaultGsiCapacity();

		CapacityProvisioner.create(
				Namespace.from(name(), definition.getIndexName()),
				
				// For textsearch-based indexes, we do not provision reads because of the
				// advanced optimizations we perform throughout the life-cycle of the index.
				
				!definition.forTextSearch()
		);

		if (definition.forTextSearch()) {

			if (this.description == null) {

				// For this scenario, we need the table's keySchema, hence this.description
				// needs to have been populated
				this.describe();
			}

			this.db.getTextSearch().getQueryModel().newQueryOptimizedGSI(
					new IndexDescriptor(name(), definition.getIndexName()),
					definition.getProjection().getNonKeyAttributes(), definition.getQueryType(),
					ItemUtils.getSchemaKey(definition.getKeySchema(), KeyType.HASH),
					ItemUtils.getSchemaKey(definition.getKeySchema(), KeyType.RANGE), 
					capacity.getReadCapacityUnits().longValue(),
					ItemUtils.getSchemaKey(this.description.getKeySchema(), KeyType.HASH),
					ItemUtils.getSchemaKey(this.description.getKeySchema(), KeyType.RANGE));
		}
		
		
		
		// If successful, push this definition to the gsiList in description

		return null;
	}

	@Override
	public void deleteGSI(IndexDescriptor index) {

		// If this is a search optimized index, also delete as well
		// in our text search tables

	}

	@Override
	public TableDescription updateTable(UpdateTableSpec update) {

		return null;
	}

	@Override
	public TableDescription waitForActive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableDescription waitForDelete() {
		// TODO Auto-generated method stub
		return null;
	}

}
