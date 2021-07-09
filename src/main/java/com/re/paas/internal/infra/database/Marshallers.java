package com.re.paas.internal.infra.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.document.ItemUtils;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteRequest;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.PutRequest;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.database.HandlesThrouphput;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.AttributeDefinition;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.BatchGetItemResult;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.BatchWriteItemResult;
import com.re.paas.api.infra.database.model.CapacityUnits;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDescription;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.IndexStatus;
import com.re.paas.api.infra.database.model.KeySchemaElement;
import com.re.paas.api.infra.database.model.KeyType;
import com.re.paas.api.infra.database.model.LocalSecondaryIndexDescription;
import com.re.paas.api.infra.database.model.Projection;
import com.re.paas.api.infra.database.model.ProjectionType;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ReturnValue;
import com.re.paas.api.infra.database.model.ScanResult;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.TableStatus;
import com.re.paas.api.infra.database.model.WriteRequest;
import com.re.paas.api.infra.database.textsearch.QueryType;
import com.re.paas.internal.infra.database.tables.attributes.IndexPartitionSpec;
import com.re.paas.internal.infra.database.tables.attributes.IndexPropertySpec;

public class Marshallers {

	private static AttributeDefinition fromAttributeDefinition(
			com.amazonaws.services.dynamodbv2.model.AttributeDefinition a) {
		return new AttributeDefinition(a.getAttributeName(), a.getAttributeType());
	}

	private static KeySchemaElement fromKeySchemaElement(com.amazonaws.services.dynamodbv2.model.KeySchemaElement a) {
		return new KeySchemaElement(a.getAttributeName(), KeyType.valueOf(a.getKeyType()));
	}

	private static PrimaryKey fromPrimaryKey(com.amazonaws.services.dynamodbv2.document.PrimaryKey a) {
		PrimaryKey b = new PrimaryKey();

		a.getComponents().forEach(ka -> {
			b.addComponent(ka.getName(), ka.getValue());
		});

		return b;
	}

	private static com.amazonaws.services.dynamodbv2.document.PrimaryKey fromAttributeValueMap(
			Map<String, AttributeValue> a) {
		com.amazonaws.services.dynamodbv2.document.PrimaryKey r = new com.amazonaws.services.dynamodbv2.document.PrimaryKey();
		a.forEach((k, v) -> {
			r.addComponent(k, ItemUtils.toSimpleValue(v));
		});
		return r;
	}

	private static com.amazonaws.services.dynamodbv2.document.PrimaryKey toPrimaryKey(PrimaryKey a) {
		com.amazonaws.services.dynamodbv2.document.PrimaryKey b = new com.amazonaws.services.dynamodbv2.document.PrimaryKey();

		a.getComponents().forEach(ka -> {
			b.addComponent(ka.getName(), ka.getValue());
		});

		return b;
	}

	private static Object fromAttributeValue(AttributeValue a) {

		Object[] values = new Object[] { a.getB(), a.getBS(), a.getBOOL(), a.getL(), a.getM(),
				a.getNULL() ? Null.value : null, a.getN(), a.getNS(), a.getS(), a.getSS(), };

		Object r = null;

		for (Object v : values) {
			if (v != null) {
				r = v;
				break;
			}
		}

		if (r == Null.value) {
			r = null;
		}

		return r;
	}

	private static Map<String, AttributeValue> toAttributeValueMap(PrimaryKey a) {

		Map<String, AttributeValue> r = new HashMap<>(2);

		a.getComponents().forEach(ka -> {
			r.put(ka.getName(), com.amazonaws.services.dynamodbv2.document.ItemUtils.toAttributeValue(ka.getValue()));
		});

		return r;
	}

	private static Map<String, AttributeValue> toAttributeValueMap(Item a) {

		Map<String, AttributeValue> r = new HashMap<>(2);

		a.asMap().forEach((k, v) -> {
			r.put(k, com.amazonaws.services.dynamodbv2.document.ItemUtils.toAttributeValue(v));
		});

		return r;
	}

	private static KeysAndAttributes toKeysAndAttributes(GetItemsSpec a) {
		return new KeysAndAttributes().withConsistentRead(a.isConsistentRead())
				.withExpressionAttributeNames(a.getNameMap()).withProjectionExpression(a.getProjectionExpression())
				.withKeys(
						a.getPrimaryKeys().stream().map(Marshallers::toAttributeValueMap).collect(Collectors.toList()));
	}

	private static GetItemsSpec fromKeysAndAttributes(KeysAndAttributes a) {

		List<PrimaryKey> keys = a.getKeys().stream().map(Marshallers::fromAttributeValueMap)
				.map(Marshallers::fromPrimaryKey).collect(Collectors.toList());

		return new GetItemsSpec(a.getProjectionExpression(), a.getExpressionAttributeNames())
				.setConsistentRead(a.isConsistentRead()).setPrimaryKeys(keys);
	}

	public static Item toItem(Map<String, AttributeValue> a) {
		Item r = new Item();
		a.forEach((k, v) -> {
			r.with(k, fromAttributeValue(v));
		});
		return r;
	}

	private static com.amazonaws.services.dynamodbv2.model.WriteRequest toWriteRequest(WriteRequest a) {
		return a.getPutRequest() != null
				? new com.amazonaws.services.dynamodbv2.model.WriteRequest(
						new PutRequest(toAttributeValueMap(a.getPutRequest())))
				: new com.amazonaws.services.dynamodbv2.model.WriteRequest(
						new DeleteRequest(toAttributeValueMap(a.getDeleteRequest())));
	}

	private static WriteRequest fromWriteRequest(com.amazonaws.services.dynamodbv2.model.WriteRequest a) {
		return a.getPutRequest() != null ? new WriteRequest(toItem(a.getPutRequest().getItem()))
				: new WriteRequest(fromPrimaryKey(fromAttributeValueMap(a.getDeleteRequest().getKey())));
	}

	public static com.amazonaws.services.dynamodbv2.model.KeySchemaElement toKeySchemaElement(KeySchemaElement a) {
		return new com.amazonaws.services.dynamodbv2.model.KeySchemaElement(a.getAttributeName(),
				a.getKeyType().toString());
	}

	public static com.amazonaws.services.dynamodbv2.model.AttributeDefinition toAttributeDefinition(
			AttributeDefinition a) {
		return new com.amazonaws.services.dynamodbv2.model.AttributeDefinition(a.getAttributeName(),
				a.getAttributeType().toString());
	}
	
	public static TableDescription fromTableDescription(Database db,
			com.amazonaws.services.dynamodbv2.model.TableDescription a) {

		// For query optimized GSIs, this gets the query type
		Table indexTable = db.getTable(IndexPropertySpec.TABLE_NAME);

		TableDescription b = new TableDescription();
		b.setAttributeDefinitions(a.getAttributeDefinitions().stream().map(Marshallers::fromAttributeDefinition)
				.collect(Collectors.toList()));
		;
		b.setCreationDateTime(a.getCreationDateTime());
		b.setGlobalSecondaryIndexes(a.getGlobalSecondaryIndexes().stream().map((c) -> {

			var indexSpec = indexTable.getItem(GetItemSpec.forKey(IndexPartitionSpec.ID,
					new IndexDescriptor(a.getTableName(), c.getIndexName()).toString(), IndexPropertySpec.QUERY_TYPE));

			return Marshallers.fromGlobalSecondaryIndexDescription(a.getTableName(), indexSpec != null,
					indexSpec != null ? QueryType.parse(indexSpec.getString(IndexPropertySpec.QUERY_TYPE)) : null, c);
		}).collect(Collectors.toList()));

		b.setItemCount(a.getItemCount());
		b.setTableName(a.getTableName());
		b.setTableSizeBytes(a.getTableSizeBytes());
		b.setTableStatus(TableStatus.valueOf(a.getTableStatus()));

		return b;
	}

	public static GlobalSecondaryIndexDescription fromGlobalSecondaryIndexDescription(String tableName,
			Boolean forTextSearch, QueryType queryType,
			com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndexDescription a) {

		return (GlobalSecondaryIndexDescription) new GlobalSecondaryIndexDescription(tableName, a.getIndexName())
				.setItemCount(a.getItemCount()).setBackfilling(a.isBackfilling())
				.setIndexStatus(IndexStatus.valueOf(a.getIndexStatus())).setIndexSizeBytes(a.getIndexSizeBytes())
				.setQueryOptimzed(forTextSearch).setQueryType(queryType)
				.setKeySchema(
						a.getKeySchema().stream().map(Marshallers::fromKeySchemaElement).collect(Collectors.toList()))
				.setProjection(new Projection(ProjectionType.fromValue(a.getProjection().getProjectionType()),
						a.getProjection().getNonKeyAttributes()));
	}

	public static LocalSecondaryIndexDescription fromLocalSecondaryIndexDescription(String tableName,
			com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndexDescription a) {

		return (LocalSecondaryIndexDescription) new LocalSecondaryIndexDescription(tableName, a.getIndexName())
				.setItemCount(a.getItemCount()).setIndexSizeBytes(a.getIndexSizeBytes())
				.setKeySchema(
						a.getKeySchema().stream().map(Marshallers::fromKeySchemaElement).collect(Collectors.toList()))
				.setProjection(new Projection(ProjectionType.fromValue(a.getProjection().getProjectionType()),
						a.getProjection().getNonKeyAttributes()));
	}

	public static com.amazonaws.services.dynamodbv2.document.spec.QuerySpec toQuerySpec(QuerySpec a) {
		return new com.amazonaws.services.dynamodbv2.document.spec.QuerySpec()
				.withKeyConditionExpression(a.getKeyConditionExpression())
				.withProjectionExpression(a.getProjectionExpression()).withFilterExpression(a.getFilterExpression())
				.withNameMap(a.getNameMap()).withValueMap(a.getValueMap())
				.withExclusiveStartKey(toPrimaryKey(a.getExclusiveStartKey())).withConsistentRead(a.isConsistentRead())
				.withScanIndexForward(a.getScanIndexForward()).withSelect(Select.fromValue(a.getSelect().toString()))
				.withMaxResultSize(a.getResultLimit()).withMaxPageSize(a.getPageLimit())
				.withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
	}

	public static QueryResult fromQueryResult(com.amazonaws.services.dynamodbv2.model.QueryResult a) {

		QueryResult b = new QueryResult();

		PrimaryKey lastEvaluatedKey = new PrimaryKey();

		a.getLastEvaluatedKey().entrySet().forEach(e -> {
			lastEvaluatedKey.addComponent(e.getKey(), fromAttributeValue(e.getValue()));
		});

		List<Item> items = a.getItems().parallelStream().map(m -> {

			Map<String, Object> n = new HashMap<>(m.size());

			m.entrySet().forEach(e -> {
				n.put(e.getKey(), fromAttributeValue(e.getValue()));
			});

			return Item.fromMap(n);
		}).collect(Collectors.toList());

		b.setItems(items).setCount(a.getCount()).setScannedCount(a.getScannedCount())
				.setLastEvaluatedKey(lastEvaluatedKey);

		return b;

	}

	public static com.amazonaws.services.dynamodbv2.document.spec.ScanSpec toScanSpec(ScanSpec a) {
		return new com.amazonaws.services.dynamodbv2.document.spec.ScanSpec()
				.withProjectionExpression(a.getProjectionExpression()).withFilterExpression(a.getFilterExpression())
				.withNameMap(a.getNameMap()).withValueMap(a.getValueMap())
				.withExclusiveStartKey(toPrimaryKey(a.getExclusiveStartKey())).withConsistentRead(a.isConsistentRead())
				.withSelect(Select.fromValue(a.getSelect().toString())).withMaxPageSize(a.getPageLimit())
				.withMaxResultSize(a.getResultLimit()).withTotalSegments(a.getTotalSegments())
				.withSegment(a.getSegment()).withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
	}

	public static ScanResult fromScanResult(com.amazonaws.services.dynamodbv2.model.ScanResult a) {

		ScanResult b = new ScanResult();

		PrimaryKey lastEvaluatedKey = new PrimaryKey();

		a.getLastEvaluatedKey().entrySet().forEach(e -> {
			lastEvaluatedKey.addComponent(e.getKey(), fromAttributeValue(e.getValue()));
		});

		List<Item> items = a.getItems().parallelStream().map(m -> {

			Map<String, Object> n = new HashMap<>(m.size());

			m.entrySet().forEach(e -> {
				n.put(e.getKey(), fromAttributeValue(e.getValue()));
			});

			return Item.fromMap(n);
		}).collect(Collectors.toList());

		b.setItems(items).setCount(a.getCount()).setScannedCount(a.getScannedCount())
				.setLastEvaluatedKey(lastEvaluatedKey);

		return b;

	}

	public static com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest toBatchGetItemRequest(
			BatchGetItemRequest a) {
		com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest b = new com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest()
				.withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

		a.getRequestItems().forEach((k, v) -> {
			b.addRequestItemsEntry(k, toKeysAndAttributes(v));
		});

		return b;
	}

	@HandlesThrouphput
	public static BatchGetItemResult fromBatchGetItemResult(
			com.amazonaws.services.dynamodbv2.model.BatchGetItemResult a) {

		Map<String, List<Item>> responses = new HashMap<>();
		BatchGetItemRequest unprocessedKeys = new BatchGetItemRequest();

		a.getResponses().entrySet().forEach(e -> {
			responses.put(e.getKey(), e.getValue().stream().map(Marshallers::toItem).collect(Collectors.toList()));
		});

		a.getUnprocessedKeys().forEach((k, v) -> {
			unprocessedKeys.addRequestItem(k, fromKeysAndAttributes(v));
		});

		return new BatchGetItemResult().setResponses(responses).setUnprocessedKeys(unprocessedKeys);
	}

	public static com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest toBatchWriteItemRequest(
			BatchWriteItemRequest a) {
		com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest b = new com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest()
				.withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

		a.getRequestItems().forEach((k, v) -> {
			b.addRequestItemsEntry(k, v.stream().map(Marshallers::toWriteRequest).collect(Collectors.toList()));
		});

		return b;
	}

	@HandlesThrouphput
	public static BatchWriteItemResult fromBatchWriteItemResult(
			com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult a) {

		BatchWriteItemRequest unprocessedKeys = new BatchWriteItemRequest();

		a.getUnprocessedItems().forEach((k, v) -> {
			if (!v.isEmpty()) {
				unprocessedKeys.addRequestItems(k,
						v.stream().map(Marshallers::fromWriteRequest).collect(Collectors.toList()));
			}
		});

		return new BatchWriteItemResult(unprocessedKeys.getRequestItems().isEmpty() ? null : unprocessedKeys);
	}

	public static com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec toPutItemSpec(PutItemSpec a) {

		ReturnValue returnValues = a.getReturnValues();

		if (returnValues != ReturnValue.NONE && returnValues != ReturnValue.ALL_OLD) {
			Exceptions.throwRuntime(new IllegalArgumentException("Unexpected returnValue: " + returnValues));
		}

		return new com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec()
				.withItem(ItemUtils.toItem(toAttributeValueMap(a.getItem())))
				.withReturnValues(
						com.amazonaws.services.dynamodbv2.model.ReturnValue.fromValue(returnValues.toString()))
				.withConditionExpression(a.getConditionExpression()).withNameMap(a.getNameMap())
				.withValueMap(a.getValueMap()).withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
	}

	public static com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec toGetItemSpec(GetItemSpec a) {
		return new com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec()
				.withPrimaryKey(toPrimaryKey(a.getPrimaryKey())).withConsistentRead(a.isConsistentRead())
				.withProjectionExpression(a.getProjectionExpression()).withNameMap(a.getNameMap())
				.withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
	}

	public static com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec toUpdateItemSpec(UpdateItemSpec a) {
		return new com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec()
				.withPrimaryKey(toPrimaryKey(a.getPrimaryKey()))
				.withReturnValues(
						com.amazonaws.services.dynamodbv2.model.ReturnValue.fromValue(a.getReturnValues().toString()))
				.withUpdateExpression(a.getUpdateExpression()).withConditionExpression(a.getConditionExpression())
				.withNameMap(a.getNameMap()).withValueMap(a.getValueMap())
				.withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
	}

	public static com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec toDeleteItemSpec(DeleteItemSpec a) {
		return new com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec()
				.withPrimaryKey(toPrimaryKey(a.getPrimaryKey()))
				.withReturnValues(
						com.amazonaws.services.dynamodbv2.model.ReturnValue.fromValue(a.getReturnValues().toString()))
				.withConditionExpression(a.getConditionExpression()).withNameMap(a.getNameMap())
				.withValueMap(a.getValueMap()).withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
	}

	public static CapacityUnits fromConsumedCapacity(com.amazonaws.services.dynamodbv2.model.ConsumedCapacity a) {
		return new CapacityUnits(a.getReadCapacityUnits().doubleValue(), a.getWriteCapacityUnits().doubleValue());
	}
}