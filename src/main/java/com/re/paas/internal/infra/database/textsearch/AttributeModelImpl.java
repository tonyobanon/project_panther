package com.re.paas.internal.infra.database.textsearch;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.KeyAttribute;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.textsearch.PartitionSizeExceeded;
import com.re.paas.api.infra.database.textsearch.QueryModel;
import com.re.paas.api.infra.database.textsearch.QueryType;
import com.re.paas.api.logging.Logger;
import com.re.paas.internal.infra.database.tables.attributes.IndexEntrySpec;

class AttributeModelImpl implements AttributeModel {

	private static Logger LOG = Logger.get(AttributeModelImpl.class);
	

	// K: table/rangeKey, V: indexName
	protected static Map<String, String> rangeKeyToIndexName = Collections.synchronizedMap(new HashMap<>());

	// K: indexId, V: hashKey
	protected static Map<String, String> indexToHashKey = Collections.synchronizedMap(new HashMap<>());

	// K: indexId, V: hashKey
	protected static Map<String, Collection<String>> indexToNonKeyProjections = Collections
			.synchronizedMap(new HashMap<>());

	// K: indexId, V: readThroughput
	protected static Map<String, Long> indexReadThroughput = Collections.synchronizedMap(new HashMap<>());

	// K: tableName, V: hashKey
	protected static Map<String, String> tableToHashKey = Collections.synchronizedMap(new HashMap<>());

	// K: tableName, V: rangeKey
	protected static Map<String, String> tableToRangeKey = Collections.synchronizedMap(new HashMap<>());

	private final QueryModel queryModel;
	
	private QueryModel getQueryModel() {
		return queryModel;
	}
	
	AttributeModelImpl(QueryModel queryModel) {
		this.queryModel = queryModel;
	}
	
	@Override
	public void addIndex(IndexDescriptor index, List<String> projections, String hashAttribute,
			String rangeAttribute, Long readThroughputCapacity, QueryType queryType, String tableHashKey,
			String tableRangeKey) {

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

		if (!tableToHashKey.containsKey(index.getTableName())) {
			tableToHashKey.put(index.getTableName(), tableHashKey);
		}

		if (tableRangeKey != null && !tableToRangeKey.containsKey(index.getTableName())) {
			tableToRangeKey.put(index.getTableName(), tableRangeKey);
		}
	}

	@Override
	public Integer getIndexSize(IndexDescriptor index, String indexRangeKey, Object indexRangeKeyValue, Item item) {

		if (indexRangeKeyValue == null) {
			return 0;
		}

		int indexSize = 0;

		String tableHashKey = AttributeModelImpl.tableToHashKey.get(index.getTableName());
		Object tableHashKeyValue = item.get(tableHashKey);

		String tableRangeKey = AttributeModelImpl.tableToRangeKey.get(index.getTableName());
		Object tableRangeKeyValue = null;

		if (tableRangeKey != null) {
			tableRangeKeyValue = item.get(tableRangeKey);
		}

		indexSize += AttributeUtils.stringSize(tableHashKey);
		indexSize += AttributeUtils.getValueSize(tableHashKeyValue);

		if (tableRangeKey != null) {
			indexSize += AttributeUtils.stringSize(tableRangeKey);
			indexSize += AttributeUtils.getValueSize(tableRangeKeyValue);
		}

		indexSize += AttributeUtils.stringSize(indexRangeKey);
		indexSize += AttributeUtils.getValueSize(indexRangeKeyValue);

		indexSize += AttributeUtils.stringSize(AttributeModelImpl.indexToHashKey.get(index.getId()));
		indexSize += AttributeUtils.numberSize(getQueryModel().partitionCount(index) + 1);

		for (String projection : AttributeModelImpl.indexToNonKeyProjections.get(index.getId())) {
			indexSize += AttributeUtils.stringSize(projection);
			indexSize += AttributeUtils.getValueSize(item.get(projection));
		}

		return indexSize;
	}

	@Override
	public void putValue(String tableName, Item item) {

		if (!tableToHashKey.containsKey(tableName)) {
			return;
		}

		for (Entry<String, Object> e : item.attributes()) {

			String indexRangeKey = e.getKey();

			if (rangeKeyToIndexName.containsKey(tableName + "/" + indexRangeKey)) {

				TablePrimaryKey tableKey = new TablePrimaryKey(tableName, item);

				Object indexRangeKeyValue = e.getValue();

				LOG.info("--->  Putting value: " + indexRangeKeyValue + " into " + tableName + "/" + indexRangeKey);

				// Get the indexId
				IndexDescriptor index = IndexDescriptor
						.fromId(tableName + "/" + rangeKeyToIndexName.get(tableName + "/" + indexRangeKey));

				// Get the entry Id
				String entryId = index.getTableName() + "/" + (String) tableKey.getHashValue();

				GetItemSpec spec = new ExpressionSpecBuilder().buildForGetItem().setPrimaryKey(new PrimaryKey(
						IndexEntrySpec.ENTRY_ID, entryId, IndexEntrySpec.INDEX_NAME, index.getIndexName()));
				Item indexEntry = Database.get().getTable(IndexEntrySpec.TABLE_NAME).getItem(spec);

				Integer itemSize = this.getIndexSize(index, indexRangeKey, indexRangeKeyValue, item);

				if (indexEntry == null) {
					addIndexEntry(index, tableKey, itemSize);
				} else {
					updateIndexEntrySize(index, tableKey, itemSize);
				}
			}
		}
	}

	private void addIndexEntry(IndexDescriptor index, TablePrimaryKey tableKey, int itemSize) {

		if (itemSize == 0) {
			return;
		}

		// Get the entry Id
		String entryId = index.getTableName() + "/" + (String) tableKey.getHashValue();

		// Generate partition Id
		int partitionId = getQueryModel().nextPartitionKey(index, itemSize);

		UpdateItemSpec spec = new ExpressionSpecBuilder()
				.addUpdate(N(indexToHashKey.get(index.getId())).set(partitionId)).buildForUpdate();

		// Update the hash attribute of the index
		if (tableKey.getRangeKey() != null) {

			Database.get().getTable(index.getTableName())
					.updateItem(spec.setPrimaryKey(new PrimaryKey(tableKey.getHashKey(), tableKey.getHashValue(),
							tableKey.getRangeKey(), tableKey.getRangeValue())));

		} else {

			Database.get().getTable(index.getTableName())
					.updateItem(spec.setPrimaryKey(new PrimaryKey(tableKey.getHashKey(), tableKey.getHashValue())));
		}

		// Add entry to the IndexEntry table
		Item newEntry = new Item()
				.withPrimaryKey(IndexEntrySpec.ENTRY_ID, entryId, IndexEntrySpec.INDEX_NAME, index.getIndexName())
				.withNumber(IndexEntrySpec.PARTITION_ID, partitionId).withInt(IndexEntrySpec.SIZE, itemSize);

		Database.get().getTable(IndexEntrySpec.TABLE_NAME)
				.putItem(new ExpressionSpecBuilder().buildForPut().withItem(newEntry));

		// Increment partition size
		getQueryModel().incrementPartition(index, partitionId, itemSize);

		LOG.info("--->  Added index entry for index " + index.getId() + ", with size: " + itemSize);
	}

	private void updateIndexEntrySize(IndexDescriptor index, TablePrimaryKey tableKey, int itemSize) {

		if (itemSize == 0) {

			// since itemSize is 0, then indexRangeKeyValue is null
			// Also set the hashKey to null, so that it's removed from the index, so as to
			// take advantage of sparse indexing

			UpdateItemSpec spec = new ExpressionSpecBuilder().addUpdate(N(indexToHashKey.get(index.getId())).remove())
					.buildForUpdate();

			// Update the hash attribute of the index
			if (tableKey.getRangeKey() != null) {

				Database.get().getTable(index.getTableName())
						.updateItem(spec.setPrimaryKey(new PrimaryKey(tableKey.getHashKey(), tableKey.getHashValue(),
								tableKey.getRangeKey(), tableKey.getRangeValue())));

			} else {

				Database.get().getTable(index.getTableName())
						.updateItem(spec.setPrimaryKey(new PrimaryKey(tableKey.getHashKey(), tableKey.getHashValue())));

			}

			// Then remove from partition
			deleteValue(index.getTableName(), tableKey.getHashValue(), index.getIndexName());
			return;
		}

		// Get the entry Id
		String entryId = index.getTableName() + "/" + (String) tableKey.getHashValue();

		Table indexEntryTable = Database.get().getTable(IndexEntrySpec.TABLE_NAME);

		Item indexEntry = indexEntryTable.getItem(new ExpressionSpecBuilder().buildForGetItem().setPrimaryKey(
				new PrimaryKey(IndexEntrySpec.ENTRY_ID, entryId, IndexEntrySpec.INDEX_NAME, index.getIndexName())));

		boolean partitionChanged = false;

		int partitionId = indexEntry.getInt(IndexEntrySpec.PARTITION_ID);

		int currentItemSize = indexEntry.getInt(IndexEntrySpec.SIZE);

		int sizeOffset = itemSize - currentItemSize;

		if (sizeOffset < 0) {
			LOG.info("--->  Decrementing partition " + partitionId + " for index " + index.getId() + " by " + " size: "
					+ (sizeOffset - sizeOffset - sizeOffset));
			System.out.println("Is " + sizeOffset + " === " + (sizeOffset - sizeOffset - sizeOffset));

			getQueryModel().decrementPartition(index, partitionId, sizeOffset - sizeOffset - sizeOffset);

		} else {

			try {
				LOG.info("--->  Incrementing partition " + partitionId + " for index " + index.getId() + " by "
						+ " size: " + sizeOffset);
				getQueryModel().incrementPartition(index, partitionId, sizeOffset);

			} catch (PartitionSizeExceeded ex) {

				// Reassign this entry to another partition

				// But first free up used space on current partition
				getQueryModel().decrementPartition(index, partitionId, currentItemSize);

				// Then re-assign

				// Generate partition Id
				partitionId = getQueryModel().nextPartitionKey(index, itemSize);

				UpdateItemSpec spec = new ExpressionSpecBuilder()
						.addUpdate(N(indexToHashKey.get(index.getId())).set(partitionId)).buildForUpdate();

				// Update the hash attribute of the index
				if (tableKey.getRangeKey() != null) {

					Database.get().getTable(index.getTableName())
							.updateItem(spec.setPrimaryKey(new PrimaryKey(tableKey.getHashKey(),
									tableKey.getHashValue(), tableKey.getRangeKey(), tableKey.getRangeValue())));

				} else {

					Database.get().getTable(index.getTableName()).updateItem(
							spec.setPrimaryKey(new PrimaryKey(tableKey.getHashKey(), tableKey.getHashValue())));

				}

				// Increment partition size
				getQueryModel().incrementPartition(index, partitionId, itemSize);

				partitionChanged = true;

			}
		}

		// Update Entry

		ExpressionSpecBuilder specExpr = new ExpressionSpecBuilder().addUpdate(N(IndexEntrySpec.SIZE).set(itemSize));

		if (partitionChanged) {

			specExpr.addUpdate(N(IndexEntrySpec.PARTITION_ID).set(partitionId));
		}

		UpdateItemSpec spec = specExpr.buildForUpdate().setPrimaryKey(
				new PrimaryKey(IndexEntrySpec.ENTRY_ID, entryId, IndexEntrySpec.INDEX_NAME, index.getIndexName()));

		indexEntryTable.updateItem(spec);
	}

	@Override
	public void updateValue(String tableName, Item item, String hashKey, Object hashValue, String rangeKey,
			Object rangeValue) {

		if (!tableToHashKey.containsKey(tableName)) {
			return;
		}

		for (Entry<String, Object> update : item.attributes()) {

			String indexRangeKey = update.getKey();

			if (rangeKeyToIndexName.containsKey(tableName + "/" + indexRangeKey)) {

				TablePrimaryKey tableKey = new TablePrimaryKey(hashKey, hashValue, rangeKey, rangeValue);

				Object indexRangeKeyValue = update.getValue();

				// Get the indexId
				IndexDescriptor index = IndexDescriptor
						.fromId(tableName + "/" + rangeKeyToIndexName.get(tableName + "/" + indexRangeKey));

				Integer itemSize = calculateIndexSizeOffset(index, indexRangeKey, indexRangeKeyValue, item);

				updateIndexEntrySize(index, tableKey, itemSize);
			}

		}
	}

	@Override
	public void deleteValue(String tableName, KeyAttribute... keys) {
		if (!tableToHashKey.containsKey(tableName)) {
			return;
		}

		TablePrimaryKey tableKey = new TablePrimaryKey(tableName, keys);

		deleteValue(tableName, tableKey.getHashValue());
	}

	@Override
	public void deleteValue(String tableName, Collection<KeyAttribute> keys) {

		if (!tableToHashKey.containsKey(tableName)) {
			return;
		}

		TablePrimaryKey tableKey = new TablePrimaryKey(tableName, keys);

		deleteValue(tableName, tableKey.getHashValue());
	}

	@Override
	public void deleteValue(String tableName, Object tableHashValue) {
		deleteValue(tableName, tableHashValue, null);
	}

	@Override
	public void deleteValue(String tableName, Object tableHashValue, String indexName) {

		String entryId = tableName + "/" + (String) tableHashValue;

		Table indexEntryTable = Database.get().getTable(IndexEntrySpec.TABLE_NAME);

		ExpressionSpecBuilder specExpr = new ExpressionSpecBuilder()
				.withKeyCondition(S(IndexEntrySpec.ENTRY_ID).eq(entryId));

		if (indexName != null) {
			specExpr.withCondition(S(IndexEntrySpec.INDEX_NAME).eq(indexName));
		}

		QuerySpec qSpec = specExpr.buildForQuery();

		indexEntryTable.query(qSpec).getItems().forEach(o -> {

			IndexDescriptor index = IndexDescriptor
					.fromId(tableName + "/" + o.getString(IndexEntrySpec.INDEX_NAME));
			int partitionId = o.getInt(IndexEntrySpec.PARTITION_ID);
			int size = o.getInt(IndexEntrySpec.SIZE);

			// Reduce partition size
			getQueryModel().decrementPartition(index, partitionId, size);

			// Remove from index entry table

			DeleteItemSpec dSpec = new ExpressionSpecBuilder().buildForDeleteItem().setPrimaryKey(new PrimaryKey(
					IndexEntrySpec.ENTRY_ID, entryId, IndexEntrySpec.INDEX_NAME, index.getIndexName()));

			indexEntryTable.deleteItem(dSpec);
		});;
		;
	}
	
	private int calculateIndexSizeOffset(IndexDescriptor index, String indexRangeKey, Object indexRangeKeyValue,
			Item item) {
	
		if (indexRangeKeyValue == null) {
			return 0;
		}
		
		int indexSize = 0;
	
		String tableHashKey = AttributeModelImpl.tableToHashKey.get(index.getTableName());
		Object tableHashKeyValue = item.get(tableHashKey);
	
		String tableRangeKey = AttributeModelImpl.tableToRangeKey.get(index.getTableName());
		Object tableRangeKeyValue = null;
	
		if (tableRangeKey != null) {
			tableRangeKeyValue = item.get(tableRangeKey);
		}
	
		indexSize += AttributeUtils.stringSize(tableHashKey);
		indexSize += AttributeUtils.getValueSize(tableHashKeyValue);
	
		if (tableRangeKey != null) {
			indexSize += AttributeUtils.stringSize(tableRangeKey);
			indexSize += AttributeUtils.getValueSize(tableRangeKeyValue);
		}
	
		indexSize += AttributeUtils.stringSize(indexRangeKey);
		indexSize += AttributeUtils.getValueSize(indexRangeKeyValue);
	
		indexSize += AttributeUtils.stringSize(AttributeModelImpl.indexToHashKey.get(index.getId()));
		indexSize += AttributeUtils.numberSize(getQueryModel().partitionCount(index) + 1);
	
		for (String projection : AttributeModelImpl.indexToNonKeyProjections.get(index.getId())) {
			indexSize += AttributeUtils.stringSize(projection);
			indexSize += AttributeUtils.getValueSize(item.get(projection));
		}
	
		return indexSize;
	}

}
