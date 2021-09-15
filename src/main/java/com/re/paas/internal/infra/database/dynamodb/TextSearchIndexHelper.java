package com.re.paas.internal.infra.database.dynamodb;

import static com.re.paas.api.infra.database.document.xspec.QueryBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.QueryBuilder.S;
import static com.re.paas.internal.infra.database.dynamodb.classes.AttributeUtils.getValueSize;
import static com.re.paas.internal.infra.database.dynamodb.classes.AttributeUtils.numberSize;
import static com.re.paas.internal.infra.database.dynamodb.classes.AttributeUtils.stringSize;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.xspec.QueryBuilder;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.BatchWriteItemSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.WriteRequest;
import com.re.paas.api.infra.database.textsearch.QueryType;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.infra.database.dynamodb.classes.ReturnValue;
import com.re.paas.internal.infra.database.dynamodb.classes.TablePrimaryKey;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.IndexEntrySpec;

class TextSearchIndexHelper {

	private static Logger LOG = Logger.get(TextSearchIndexHelper.class);

	// K: table/rangeKey, V: indexName
	private static Map<String, String> rangeKeyToIndexName = Collections.synchronizedMap(new HashMap<>());

	// K: indexId, V: hashKey
	static Map<String, String> indexToHashKey = Collections.synchronizedMap(new HashMap<>());

	// K: indexId, V: hashKey
	private static Map<String, Collection<String>> indexToNonKeyProjections = Collections
			.synchronizedMap(new HashMap<>());

	// K: indexId, V: readThroughput
	static Map<String, Long> indexReadThroughput = Collections.synchronizedMap(new HashMap<>());

	private static HashSet<String> tables = new HashSet<>();

	private final QueryInterfaceImpl queryModel;

	private QueryInterfaceImpl getQueryModel() {
		return queryModel;
	}

	private static String toEntryId(TablePrimaryKey key) {
		return key.getTableName() + "/" + key.getHashValue() + key.getHashValue() != null ? "/" + key.getRangeValue() : "";
	}

	private void deleteIndexEntries(TablePrimaryKey key, String indexName) {

		String entryId = toEntryId(key);

		TableImpl indexEntryTable = (TableImpl) Database.get().getTable(IndexEntrySpec.TABLE_NAME);

		QueryBuilder specExpr = new QueryBuilder().withKeyCondition(S(IndexEntrySpec.ENTRY_ID).eq(entryId));

		if (indexName != null) {
			specExpr.withCondition(S(IndexEntrySpec.INDEX_NAME).eq(indexName));
		}

		QuerySpec qSpec = specExpr.buildForQuery();

		BatchWriteItemSpec indexEntryDeletes = new BatchWriteItemSpec();

		indexEntryTable.query(qSpec).getItems().forEach(o -> {

			IndexDescriptor index = IndexDescriptor
					.fromId(key.getTableName() + "/" + o.getString(IndexEntrySpec.INDEX_NAME));
			int partitionId = o.getInt(IndexEntrySpec.PARTITION_ID);
			int size = o.getInt(IndexEntrySpec.SIZE);

			// Reduce partition size
			getQueryModel().decrementPartition(index, partitionId, size);

			indexEntryDeletes.addRequestItem(IndexEntrySpec.TABLE_NAME, new WriteRequest(
					new PrimaryKey(IndexEntrySpec.ENTRY_ID, entryId, IndexEntrySpec.INDEX_NAME, index.getIndexName())));
		});

		DatabaseImpl dbImpl = (DatabaseImpl) Database.get();
		dbImpl.batchWriteItem0(indexEntryDeletes);
	}

	private int getIndexEntrySize(IndexDescriptor index, String indexRangeKey, Object indexRangeKeyValue,
			Item item) {

		if (indexRangeKeyValue == null) {
			return 0;
		}

		int indexSize = 0;

		String tableHashKey = TableImpl.getHashKey(index.getTableName());
		Object tableHashKeyValue = item.get(tableHashKey);

		String tableRangeKey = TableImpl.getRangeKey(index.getTableName());
		Object tableRangeKeyValue = null;

		if (tableRangeKey != null) {
			tableRangeKeyValue = item.get(tableRangeKey);
		}

		indexSize += stringSize(tableHashKey);
		indexSize += getValueSize(tableHashKeyValue);

		if (tableRangeKey != null) {
			indexSize += stringSize(tableRangeKey);
			indexSize += getValueSize(tableRangeKeyValue);
		}

		indexSize += stringSize(indexRangeKey);
		indexSize += getValueSize(indexRangeKeyValue);

		indexSize += stringSize(TextSearchIndexHelper.indexToHashKey.get(index.getId()));
		indexSize += numberSize(getQueryModel().partitionCount(index) + 1);

		for (String projection : TextSearchIndexHelper.indexToNonKeyProjections.get(index.getId())) {
			indexSize += stringSize(projection);
			indexSize += getValueSize(item.get(projection));
		}

		return indexSize;
	}

	private void addIndexEntry(IndexDescriptor index, TablePrimaryKey tableKey, int itemSize) {

		assert itemSize != 0;

		String entryId = toEntryId(tableKey);

		// Generate partition Id
		int partitionId = this.getQueryModel().nextPartitionKey(index, itemSize);

		UpdateItemSpec spec = new QueryBuilder().addUpdate(N(TextSearchIndexHelper.indexToHashKey.get(index.getId())).set(partitionId))
				.buildForUpdate();

		TableImpl t = (TableImpl) Database.get().getTable(index.getTableName());

		// Update the hash attribute of the index

		t.updateItem0(spec.setPrimaryKey(new PrimaryKey(tableKey.getHashKey(), tableKey.getHashValue(),
				tableKey.getRangeKey(), tableKey.getRangeValue())), ReturnValue.NONE);

		// Add entry to the IndexEntry table
		Item newEntry = new Item()
				.withPrimaryKey(IndexEntrySpec.ENTRY_ID, entryId, IndexEntrySpec.INDEX_NAME, index.getIndexName())
				.withNumber(IndexEntrySpec.PARTITION_ID, partitionId).withInt(IndexEntrySpec.SIZE, itemSize);

		TableImpl indexEntryTable = (TableImpl) Database.get().getTable(IndexEntrySpec.TABLE_NAME);

		indexEntryTable.putItem0(new QueryBuilder().buildForPut().withItem(newEntry), ReturnValue.NONE);

		// Increment partition size
		getQueryModel().incrementPartition(index, partitionId, itemSize);

		LOG.info("Added index entry for index " + index.getId() + ", with size: " + itemSize);
	}

	TextSearchIndexHelper(QueryInterfaceImpl queryModel) {
		this.queryModel = queryModel;
	}

	void addIndex(IndexDescriptor index, List<String> projections, String hashAttribute, String rangeAttribute,
			Long readThroughputCapacity, QueryType queryTypey) {

		LOG.info("Adding index: " + index.getId());

		if (!rangeKeyToIndexName.containsKey(index.getTableName() + "/" + rangeAttribute)) {
			rangeKeyToIndexName.put(index.getTableName() + "/" + rangeAttribute, index.getIndexName());
		}

		if (!indexToHashKey.containsKey(index.getId())) {
			indexToHashKey.put(index.getId(), hashAttribute);
		}

		if (!indexToNonKeyProjections.containsKey(index.getId())) {
			indexToNonKeyProjections.put(index.getId(), projections);
		}

		if (!indexReadThroughput.containsKey(index.getId())) {
			indexReadThroughput.put(index.getId(), readThroughputCapacity);
		}

		tables.add(index.getTableName());
	}

	void putValue(TablePrimaryKey key, Item item) {

		if (!tables.contains(key.getTableName())) {
			return;
		}

		deleteIndexEntries(key, null);

		for (Entry<String, Object> update : item.attributes()) {

			String indexRangeKey = update.getKey();
			String indexName = rangeKeyToIndexName.get(key.getTableName() + "/" + indexRangeKey);

			if (indexName != null) {

				Object indexRangeKeyValue = update.getValue();
				IndexDescriptor index = IndexDescriptor.fromId(key.getTableName() + "/" + indexName);

				Integer itemSize = getIndexEntrySize(index, indexRangeKey, indexRangeKeyValue, item);

				LOG.trace("[putValue]: index=" + index.getId() + ", entryId=" + toEntryId(key)
						+ ", sizeOffset=" + itemSize);

				this.addIndexEntry(index, key, itemSize);
			}
		}
	}

	void deleteValue(TablePrimaryKey key) {
		deleteIndexEntries(key, null);
	}

}
