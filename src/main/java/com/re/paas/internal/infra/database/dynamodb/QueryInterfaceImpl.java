package com.re.paas.internal.infra.database.dynamodb;

import static com.re.paas.api.infra.database.document.xspec.QueryBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.QueryBuilder.S;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.Condition;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.QueryBuilder;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.textsearch.QueryInterface;
import com.re.paas.api.infra.database.textsearch.QueryType;
import com.re.paas.api.infra.database.textsearch.Checkpoint;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.infra.database.dynamodb.classes.AttributeUtils;
import com.re.paas.internal.infra.database.dynamodb.classes.PartitionSizeExceeded;
import com.re.paas.internal.infra.database.dynamodb.classes.ReturnValue;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.IndexPartitionSpec;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.IndexPropertySpec;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.IndexUPartitionSpec;

//Query Model class need to keep all partition data it uses in memory

//Optimize, Implement Optimistic Locks for partition key lookups, i.e nextPartitionKey
//Keep partition count, and Size in memory, i.e IndexPartitionTable
//In some cases don't use the Adapter in this class i.e. QueryModel to avoid unnecessary overhead

//Bug alert: When querying a single index in dynamoDBLocal from different threads using different hash keys,
//they both return the same result: typically that of the last thread that completes the query
//Ensure that this happens only for local development. A temporary fix is to set MAX_CONCURRENT_THREAD_PER_QUERY = 1

@Todo("Implement all comments in this class")

public class QueryInterfaceImpl implements QueryInterface {

	// Set to a reasonable value in production
	private static final Integer QUERY_TIMEOUT_IN_MILLIS = 10000;

	private static final Integer MAX_CONCURRENT_THREAD_PER_QUERY = 1;

	// @DEV
	// 64000
	private static final Integer MAX_PARTITION_SIZE = 250;

	private static final Map<String, Checkpoint> queryCheckpoints = Collections
			.synchronizedMap(new HashMap<String, Checkpoint>());

	// K: indexId, V: numberOfThreads being used.. --> 0 means no current query
	// is happening on index
	private static final Map<String, Integer> openQueries = Collections.synchronizedMap(new HashMap<String, Integer>());

	// This will be used later, for metrics, or throttling
	private static final Map<String, Integer> indexQueryToThreadCount = Collections
			.synchronizedMap(new HashMap<String, Integer>());

	// If true, It indicates that a call to nextPartitionKey() is in execution
	private static final Map<String, Boolean> activePartitionKeyQuery = Collections
			.synchronizedMap(new HashMap<String, Boolean>());

	private static final Logger LOG = Logger.get(QueryInterfaceImpl.class);

	private final TextSearchIndexHelper attrModel;

	TextSearchIndexHelper getIndexHelper() {
		return attrModel;
	}

	QueryInterfaceImpl() {
		attrModel = new TextSearchIndexHelper(this);
	}

	void load() {

		// Scan all entries of the IndexProperty Table to fetch indexed GSIs

		Database.get().getTable(IndexPropertySpec.TABLE_NAME).scan(new QueryBuilder().buildForScan()).getItems()
		.forEach(item -> {

			IndexDescriptor index = IndexDescriptor.fromId(item.getString(IndexPropertySpec.ID));
			String indexHashKey = item.getString(IndexPropertySpec.HASH_KEY);
			String indexRangeKey = item.getString(IndexPropertySpec.RANGE_KEY);
			List<String> projections = AttributeUtils
					.getProjections(item.get(IndexPropertySpec.PROJECTIONS));
			Long readThroughputCapacity = item.getLong(IndexPropertySpec.PROVISIONED_THROUGHPUT);
			QueryType queryType = QueryType.parse(item.getString(IndexPropertySpec.QUERY_TYPE));

			getIndexHelper().addIndex(index, projections, indexHashKey, indexRangeKey,
					readThroughputCapacity, queryType);

			openQueries.put(index.getId(), 0);
			activePartitionKeyQuery.put(index.getId(), false);
		});
	
	}

	@Override
	public final Map<String, Checkpoint> getCheckpoints() {
		return queryCheckpoints;
	}

	@Override
	public Map<String, Integer> getOpenQueries() {
		return openQueries;
	}

	@Override
	public String newSearchKey(String keyword, int limit, Integer... entityTypes) {
		String checkpointId = Utils.newRandom();
		queryCheckpoints.put(checkpointId, new Checkpoint(keyword, limit, entityTypes));
		return checkpointId;
	}

	// Find a way to throttle query for excessive queries on an index
	// Queries should emit some sort of event that can be sent to websocket
	// clients.
	// Make considerations using the index's read throughput settings

	@Override
	public void parallelQuery(IndexDescriptor index, Condition rangeCondition, Consumer<Item> consumer,
			String... projections) {

		openQueries.put(index.getId(), openQueries.get(index.getId()) + 1);

		Table queryTable = Database.get().getTable(index.getTableName());
		Index queryIndex = queryTable.getIndex(index.getIndexName());

		// long provisionedReadThroghhput =
		// AttributeModel.indexReadThroughput.get(index.getId());

		int partitionCount = partitionCount(index);

		if (partitionCount == 0) {
			return;
		}

		List<Integer> segments = new ArrayList<>();

		if (partitionCount <= MAX_CONCURRENT_THREAD_PER_QUERY) {
			segments.add(partitionCount);
		} else {

			int segmentCount = partitionCount / MAX_CONCURRENT_THREAD_PER_QUERY;

			for (int i = 0; i < segmentCount; i++) {
				segments.add(MAX_CONCURRENT_THREAD_PER_QUERY);
			}

			if (partitionCount % MAX_CONCURRENT_THREAD_PER_QUERY > 0) {
				segments.add(partitionCount % MAX_CONCURRENT_THREAD_PER_QUERY);
			}
		}

		LOG.debug("Starting query operation for index: '" + index.getId() + "' with " + partitionCount
				+ " partition(s), " + segments.size() + " segment(s)");

		int currentPartition = 0;

		for (int partitions : segments) {

			LOG.debug("Querying new segment for index: '" + index.getId() + "'. Total partitions in this segment -> "
					+ partitions);

			Thread currentThread = Thread.currentThread();

			Collection<Integer> partitionsQueried = Collections.synchronizedList(new ArrayList<>());

			for (int i = 0; i < partitions; i++) {

				// Increment current Partition
				currentPartition += 1;

				Integer _currentPartition = currentPartition;

				Thread newThread = new Thread(new Runnable() {

					@Override
					public void run() {

						partitionsQueried.add(_currentPartition);

						QueryBuilder specExpr = new QueryBuilder();

						Condition condition = N(TextSearchIndexHelper.indexToHashKey.get(index.getId()))
								.eq(_currentPartition).and(rangeCondition);
						specExpr.withKeyCondition(condition).addProjection(projections);

						LOG.debug("Query for partition " + _currentPartition + " in index '" + index.getId()
								+ "' has started");

						queryIndex.query(specExpr.buildForQuery()).getItems().forEach(consumer);

						partitionsQueried.remove(_currentPartition);

						LOG.debug("Query for partition " + _currentPartition + " in index '" + index.getId()
								+ "' has completed successfully");

						if (partitionsQueried.isEmpty()) {
							currentThread.interrupt();
						}
					}
				});

				newThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

					@Override
					public void uncaughtException(Thread t, Throwable ex) {
						LOG.fatal("The following error occurred while performing query for partition "
								+ _currentPartition + " in index '" + index.getId() + "' -> " + ex.getMessage());
					}
				});

				newThread.start();

			}

			try {

				Thread.sleep(QUERY_TIMEOUT_IN_MILLIS);

				LOG.fatal("A partition query operation in index '" + index.getId() + "' is taking too long to return");
				throw new RuntimeException(
						"A partition query operation in index '" + index.getId() + "' is taking too long to return");

			} catch (InterruptedException e) {
				continue;
			}
		}

		openQueries.put(index.getId(), openQueries.get(index.getId()) - 1);

		LOG.debug("Query operation for index: '" + index.getId() + "' with " + partitionCount + " partition(s), "
				+ segments.size() + " segment(s) has completed successfully");

	}

	void addIndex(IndexDescriptor index, List<String> projections, QueryType queryType, String indexHashKey,
			String indexRangeKey, Long readThroughputCapacity) {

		LOG.info("Registering new QueryOptimizedGSI: '" + index.getId());

		String indexId = index.getId();

		// Create entry in IndexPartition Table
		newPartition(index, 1);

		// Create entry in IndexProperty Table

		TableImpl table = (TableImpl) Database.get().getTable(IndexPropertySpec.TABLE_NAME);

		Item item = new Item().withPrimaryKey(IndexPropertySpec.ID, indexId)
				.withString(IndexPropertySpec.HASH_KEY, indexHashKey)
				.withString(IndexPropertySpec.RANGE_KEY, indexRangeKey)
				.with(IndexPropertySpec.PROJECTIONS, AttributeUtils.toProjectionString(projections))
				.withNumber(IndexPropertySpec.PROVISIONED_THROUGHPUT, readThroughputCapacity)
				.withString(IndexPropertySpec.QUERY_TYPE, queryType.toString());

		table.putItem0(new QueryBuilder().buildForPut().withItem(item), ReturnValue.NONE);

		getIndexHelper().addIndex(index, projections, indexHashKey, indexRangeKey, readThroughputCapacity,
				queryType);

		openQueries.put(index.getId(), 0);
		activePartitionKeyQuery.put(index.getId(), false);
	}

	private static void newPartition(IndexDescriptor index, Integer partitionId) {

		LOG.info("Creating partition " + partitionId + " for index '" + index.getId() + "'");

		TableImpl indexPartition = (TableImpl) Database.get().getTable(IndexPartitionSpec.TABLE_NAME);
		Item indexMapping = new Item()
				.withPrimaryKey(IndexPartitionSpec.ID, index.getId(), IndexPartitionSpec.PARTITION, partitionId)
				.withInt(IndexPartitionSpec.SIZE, 0);

		indexPartition.putItem0(new QueryBuilder().buildForPut().withItem(indexMapping), ReturnValue.NONE);
	}

	@Todo("Remove this attribute from entity if not used")
	private static QueryType getQueryType(IndexDescriptor index) {

		TableImpl table = (TableImpl) Database.get().getTable(IndexPropertySpec.TABLE_NAME);

		com.amazonaws.services.dynamodbv2.document.Item item = table
				.getItem0(new QueryBuilder().addProjection(IndexPropertySpec.QUERY_TYPE).buildForGetItem()
						.setPrimaryKey(new PrimaryKey(IndexPropertySpec.ID, index.getId())))
				.getItem();

		return QueryType.parse(item.getString(IndexPropertySpec.QUERY_TYPE));
	}

	@BlockerTodo("Partition count should be cached in memory")
	@BlockerTodo("Add an attribute to the IndexProperty table to keep track of partition count, instead of querying IndexPartition Table table")
	Integer partitionCount(IndexDescriptor index) {

		LOG.debug("Getting partition count for index '" + index.getId() + "'");

		String indexId = index.getId();

		Table table = Database.get().getTable(IndexPartitionSpec.TABLE_NAME);

		QuerySpec spec = new QueryBuilder().withKeyCondition(S(IndexPartitionSpec.ID).eq(indexId)).buildForQuery()
				.setConsistentRead(true);

		return table.query(spec).getCount();
	}

	private static Integer getPartitionSize(IndexDescriptor index, Integer partitionId) {

		LOG.debug("Getting size of partition " + partitionId + " of index '" + index.getId() + "'");

		String indexId = index.getId();

		TableImpl table = (TableImpl) Database.get().getTable(IndexPartitionSpec.TABLE_NAME);

		com.amazonaws.services.dynamodbv2.document.Item item = table
				.getItem0(new QueryBuilder().addProjection(IndexPartitionSpec.SIZE).buildForGetItem().setPrimaryKey(
						new PrimaryKey(IndexPartitionSpec.ID, indexId, IndexPartitionSpec.PARTITION, partitionId)))
				.getItem();

		return item.getInt(IndexPartitionSpec.SIZE);
	}

	Integer nextPartitionKey(IndexDescriptor index, int size) {

		LOG.debug("Getting next partition key of index '" + index.getId() + "' for item with size of " + size);

		String indexId = index.getId();

		activePartitionKeyQuery.put(indexId, true);

		// Check if any unfilled partition exists on this index

		int partitionId = getExistingFreeSpace(index, size);
		if (partitionId != -1) {
			return partitionId;
		}

		// Get current partition and size

		int currentPartionId = partitionCount(index);
		int currentPatitionSize = getPartitionSize(index, currentPartionId);

		if (currentPatitionSize + size < MAX_PARTITION_SIZE) {
			return currentPartionId;
		} else {

			// Add free space to current partition

			int freeSpace = MAX_PARTITION_SIZE - currentPatitionSize;
			if (freeSpace > 0) {
				incrementPartitionFreeSpace(index, currentPartionId, freeSpace);
			}

			// Return new partition Id

			int newPartitionKey = currentPartionId + 1;
			newPartition(index, newPartitionKey);

			return newPartitionKey;
		}
	}

	private static Integer getExistingFreeSpace(IndexDescriptor index, int size) {

		LOG.debug("Fetching free partitions of index '" + index.getId() + "' with size >= " + size);

		Table indexUPartition = Database.get().getTable(IndexUPartitionSpec.TABLE_NAME);

		QuerySpec spec = new QueryBuilder()
				.withKeyCondition(S(IndexUPartitionSpec.ID).eq(index.getId()).and(N(IndexUPartitionSpec.SIZE).ge(size)))
				.addProjection(IndexUPartitionSpec.PARTITION_ID).buildForQuery().setConsistentRead(true)
				.setResultLimit(size);

		QueryResult result = indexUPartition.getIndex(IndexUPartitionSpec.SIZE_INDEX).query(spec);

		Optional<Item> o = Optional.of(result.getItems().get(0));

		if (o.isPresent()) {
			return o.get().getInt(IndexUPartitionSpec.PARTITION_ID);
		}

		return -1;
	}

	void incrementPartition(IndexDescriptor index, int partitionId, int size) throws PartitionSizeExceeded {

		LOG.info("Increasing size of partition " + partitionId + " of index '" + index.getId() + "' by " + size);

		if (getPartitionSize(index, partitionId) + size > MAX_PARTITION_SIZE) {
			throw new PartitionSizeExceeded();
		}

		TableImpl indexPartitionTable = (TableImpl) Database.get().getTable(IndexPartitionSpec.TABLE_NAME);

		UpdateItemSpec spec = new QueryBuilder()
				.withKeyCondition(
						S(IndexPartitionSpec.ID).eq(index.getId()).and(N(IndexPartitionSpec.PARTITION).eq(partitionId)))
				.withCondition(N(IndexPartitionSpec.SIZE).le(MAX_PARTITION_SIZE - size))
				.addUpdate(N(IndexPartitionSpec.SIZE).set(N(IndexPartitionSpec.SIZE).plus(size))).buildForUpdate();

		indexPartitionTable.updateItem0(spec, ReturnValue.NONE);

		if (partitionCount(index) > partitionId) {
			decrementPartitionFreeSpace(index, partitionId, size);
		}
	}

	void decrementPartition(IndexDescriptor index, int partitionId, int size) {

		LOG.info("Reducing size of partition " + partitionId + " of index '" + index.getId() + "' by " + size);

		TableImpl indexPartitionTable = (TableImpl) Database.get().getTable(IndexPartitionSpec.TABLE_NAME);

		UpdateItemSpec spec = new QueryBuilder()
				.withKeyCondition(
						S(IndexPartitionSpec.ID).eq(index.getId()).and(N(IndexPartitionSpec.PARTITION).eq(partitionId)))
				.addUpdate(N(IndexPartitionSpec.SIZE).set(N(IndexPartitionSpec.SIZE).minus(size))).buildForUpdate();

		indexPartitionTable.updateItem0(spec, ReturnValue.NONE);

		if (partitionCount(index) > partitionId) {
			incrementPartitionFreeSpace(index, partitionId, size);
		}
	}

	private static void incrementPartitionFreeSpace(IndexDescriptor index, int partitionId, int size) {

		LOG.info("Increasing free space for partition " + partitionId + " of index '" + index.getId() + "' by " + size);

		TableImpl indexUPartitionTable = (TableImpl) Database.get().getTable(IndexUPartitionSpec.TABLE_NAME);

		GetItemSpec spec = new QueryBuilder().addProjection(IndexUPartitionSpec.ID).buildForGetItem().setPrimaryKey(
				new PrimaryKey(IndexUPartitionSpec.ID, index.getId(), IndexUPartitionSpec.PARTITION_ID, partitionId));

		boolean entryExist = indexUPartitionTable.getItem0(spec).getItem() != null;

		if (!entryExist) {

			// Add entry
			Item partitionItem = new Item()

					.withPrimaryKey(IndexUPartitionSpec.ID, index.getId(), IndexUPartitionSpec.PARTITION_ID,
							partitionId)
					.withNumber(IndexUPartitionSpec.SIZE, size);

			indexUPartitionTable.putItem0(new QueryBuilder().buildForPut().withItem(partitionItem), ReturnValue.NONE);

		} else {

			UpdateItemSpec uSpec = new QueryBuilder()
					.withKeyCondition(S(IndexUPartitionSpec.ID).eq(index.getId())
							.and(N(IndexUPartitionSpec.PARTITION_ID).eq(partitionId)))
					.addUpdate(N(IndexUPartitionSpec.SIZE).set(N(IndexUPartitionSpec.SIZE).plus(size)))
					.buildForUpdate();

			// Increment by size
			indexUPartitionTable.updateItem0(uSpec, ReturnValue.NONE);
		}
	}

	private static void decrementPartitionFreeSpace(IndexDescriptor index, int partitionId, int size) {

		LOG.info("Decreasing free space for partition " + partitionId + " of index '" + index.getId() + "' by " + size);

		TableImpl indexUPartition = (TableImpl) Database.get().getTable(IndexUPartitionSpec.TABLE_NAME);

		UpdateItemSpec uSpec = new QueryBuilder()
				.withKeyCondition(S(IndexUPartitionSpec.ID).eq(index.getId())
						.and(N(IndexUPartitionSpec.PARTITION_ID).eq(partitionId)))
				.addUpdate(N(IndexUPartitionSpec.SIZE).set(N(IndexUPartitionSpec.SIZE).minus(size))).buildForUpdate();

		// Decrement by size
		Map<String, Object> attrs = Marshallers
				.toObjectMap(indexUPartition.updateItem0(uSpec, ReturnValue.ALL_NEW).getAttributes());

		if (((Integer) attrs.get(IndexUPartitionSpec.SIZE)) == 0) {

			DeleteItemSpec dSpec = new QueryBuilder().buildForDeleteItem().setPrimaryKey(new PrimaryKey(
					IndexUPartitionSpec.ID, index.getId(), IndexUPartitionSpec.PARTITION_ID, partitionId));

			indexUPartition.deleteItem0(dSpec, ReturnValue.NONE);
		}
	}

}
