package com.re.paas.integrated.infra.database.dynamodb.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.BatchWriteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.KeysAndAttributes;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.kylantis.eaa.core.base.StorageServiceFactory;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;

public class DynamoDBUtils {

	private static final int DYNAMO_DB_BATCH_PUT_COUNT_LIMIT = 25;

	@BlockerTodo("This method needs to be tested using a large dataset")
	public static Map<Object, Object> batchGetSingleAttr(String tableName, String hashKey, String rangeKey,
			List<PrimaryKey> keys, String attrName) {

		Map<Object, Object> result = new HashMap<Object, Object>();

		TableKeysAndAttributes keyAttributes = new TableKeysAndAttributes(tableName);

		Map<String, String> nameMap = new FluentHashMap<String, String>().with("#hk", hashKey).with("#a", attrName);
		if (rangeKey != null) {
			nameMap.put("#rk", rangeKey);
		}

		keyAttributes.withProjectionExpression("#hk, #a" + (rangeKey != null ? ", #rk" : ""));
		keyAttributes.withNameMap(nameMap);
		keyAttributes.withPrimaryKeys(keys.toArray(new PrimaryKey[keys.size()]));

		Map<String, KeysAndAttributes> unProcessedKeys = null;
		do {

			BatchGetItemSpec spec = new BatchGetItemSpec();
			// .withTableKeyAndAttributes(keyAttributes);

			if (unProcessedKeys != null && unProcessedKeys.size() > 0) {
				spec.withUnprocessedKeys(unProcessedKeys);
			} else {
				spec.withTableKeyAndAttributes(keyAttributes);
			}

			BatchGetItemOutcome outcome = StorageServiceFactory.getDirectDatabase().batchGetItem(spec);

			List<Item> items = outcome.getTableItems().get(tableName);

			for (Item item : items) {
				if (item != null) {

					if (rangeKey == null) {
						result.put(item.get(hashKey), item.get(attrName));
					} else {
						result.put(item.get(rangeKey), item.get(attrName));
					}
				}
			}

			unProcessedKeys = outcome.getUnprocessedKeys();

		} while (unProcessedKeys != null && unProcessedKeys.size() > 0);

		return result;
	}

	@Todo("This is not the optimal solution")
	public static void batchDelete(String tableName, final List<PrimaryKey> primaryKeys) {

		int segments;

		if (primaryKeys.size() <= DYNAMO_DB_BATCH_PUT_COUNT_LIMIT) {
			segments = 1;
		} else {
			segments = primaryKeys.size() / DYNAMO_DB_BATCH_PUT_COUNT_LIMIT;
			if (primaryKeys.size() % DYNAMO_DB_BATCH_PUT_COUNT_LIMIT > 0) {
				segments += 1;
			}
		}

		int currentIndex = 0;

		for (int i = 0; i < segments; i++) {

			TableWriteItems items = new TableWriteItems(tableName);

			for (int j = 0; j < 25; j++) {

				if (currentIndex < primaryKeys.size()) {

					PrimaryKey o = primaryKeys.get(currentIndex);
					items.addPrimaryKeyToDelete(o);

					currentIndex += 1;
				} else {
					break;
				}
			}

			if (items.getPrimaryKeysToDelete() != null && items.getPrimaryKeysToDelete().size() > 0) {

				BatchWriteItemSpec spec = new BatchWriteItemSpec();
				spec.withTableWriteItems(items);

				StorageServiceFactory.getDocumentDatabase().batchWriteItem(spec);
			}
		}
	}

	@Todo("This is not the optimal solution")
	public static void batchPut(String tableName, final List<Item> itemList) {

		int segments;

		if (itemList.size() <= DYNAMO_DB_BATCH_PUT_COUNT_LIMIT) {
			segments = 1;
		} else {
			segments = itemList.size() / DYNAMO_DB_BATCH_PUT_COUNT_LIMIT;
			if (itemList.size() % DYNAMO_DB_BATCH_PUT_COUNT_LIMIT > 0) {
				segments += 1;
			}
		}

		int currentIndex = 0;

		for (int i = 0; i < segments; i++) {

			TableWriteItems items = new TableWriteItems(tableName);

			for (int j = 0; j < 25; j++) {

				if (currentIndex < itemList.size()) {

					Item o = itemList.get(currentIndex);
					items.addItemToPut(o);

					currentIndex += 1;
				} else {
					break;
				}
			}

			if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {

				BatchWriteItemSpec spec = new BatchWriteItemSpec();
				spec.withTableWriteItems(items);

				StorageServiceFactory.getDocumentDatabase().batchWriteItem(spec);
			}
		}
	}

	public static List<Item> batchGet(String tableName, String hashKeyName, List<Object> keys, String projectionExpr,
			Map<String, String> nameMap) {

		List<Item> result = new ArrayList<>();

		TableKeysAndAttributes keyAttributes = new TableKeysAndAttributes(tableName);
		keyAttributes.addHashOnlyPrimaryKeys(hashKeyName, keys);

		if (projectionExpr != null) {
			keyAttributes.withProjectionExpression(projectionExpr);
			keyAttributes.withNameMap(nameMap);
		}

		Map<String, KeysAndAttributes> unProcessedKeys = null;
		do {

			BatchGetItemSpec spec = new BatchGetItemSpec().withTableKeyAndAttributes(keyAttributes);

			if (unProcessedKeys != null && unProcessedKeys.size() > 0) {
				spec.withUnprocessedKeys(unProcessedKeys);
			}

			BatchGetItemOutcome outcome = StorageServiceFactory.getDirectDatabase().batchGetItem(spec);

			List<Item> items = outcome.getTableItems().get(tableName);

			for (Item item : items) {
				result.add(item);
			}

			unProcessedKeys = outcome.getUnprocessedKeys();

		} while (unProcessedKeys != null && unProcessedKeys.size() > 0);

		return result;
	}

	@Todo("Use an iterator instead for efficiency")
	public static List<Item> scanTable(String tableName, String projectionExpr, Map<String, String> nameMap,
			String... filterExpr) {

		List<Item> result = new ArrayList<>();

		ScanRequest scanRequest = new ScanRequest().withTableName(tableName);

		if (projectionExpr != null) {
			scanRequest.setProjectionExpression(projectionExpr);
			scanRequest.setExpressionAttributeNames(nameMap);
		}

		if (filterExpr.length > 0) {
			scanRequest.withFilterExpression(filterExpr[0]);
		}

		Map<String, AttributeValue> lastKeyEvaluated = null;
		do {
			scanRequest.withExclusiveStartKey(lastKeyEvaluated);

			ScanResult scanResult = StorageServiceFactory.getDatabase().scan(scanRequest);
			
			for (Map<String, AttributeValue> i : scanResult.getItems()) {
				Item item = new Item();
				for (Entry<String, AttributeValue> e : i.entrySet()) {
					item.with(e.getKey(), getAttributeValue(e.getValue()));
				}
				result.add(item);
			}
			lastKeyEvaluated = scanResult.getLastEvaluatedKey();
		} while (lastKeyEvaluated != null);

		return result;
	}

	public static List<Item> queryTable(String tableName, String projectionExpr, Map<String, String> nameMap,
			Map<String, Object> valueMap, String conditionExpr) {

		List<Item> result = new ArrayList<>();

		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(conditionExpr);
		querySpec.withProjectionExpression(projectionExpr);
		querySpec.withNameMap(nameMap);
		querySpec.withValueMap(valueMap);

		ItemCollection<QueryOutcome> queryResult = StorageServiceFactory.getDocumentDatabase().getTable(tableName)
				.query(querySpec);

		queryResult.iterator().forEachRemaining(item -> {
			result.add(item);
		});

		return result;
	}

	public static List<Item> queryIndex(com.amazonaws.services.dynamodbv2.document.Index index, String projectionExpr,
			Map<String, String> nameMap, Map<String, Object> valueMap, String conditionExpr) {

		List<Item> result = new ArrayList<>();

		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression(conditionExpr);

		if (projectionExpr != null) {
			querySpec.withProjectionExpression(projectionExpr);
		}

		querySpec.withNameMap(nameMap);
		querySpec.withValueMap(valueMap);

		ItemCollection<QueryOutcome> queryResult = index.query(querySpec);

		queryResult.iterator().forEachRemaining(item -> {
			result.add(item);
		});

		return result;
	}

	public static boolean isNumber(Class<?> clazz, String fieldName) {
		try {

			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			Class<?> fieldType = field.getType();

			if (fieldType.getSuperclass().getTypeName().equals("java.lang.Number")) {
				return true;
			}

		} catch (NoSuchFieldException e) {
			Exceptions.throwRuntime(e);
		}

		return false;
	}

	public static String getScalarType(Class<?> clazz, String fieldName) {

		Field field = null;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			Exceptions.throwRuntime(e);
		}

		field.setAccessible(true);
		Class<?> fieldType = field.getType();

		if (fieldType.getTypeName().equals("java.nio.ByteBuffer")) {
			return "B";
		}

		try {
			if (fieldType.getSuperclass().getTypeName().equals("java.lang.Number")) {
				return "N";
			}
		} catch (NullPointerException e) {
		}

		if (fieldType.getTypeName().equals("java.lang.String")) {
			return "S";
		}

		throw new RuntimeException(
				"Could not determine scalar type for " + clazz.getSimpleName() + "/" + field.getName());
	}

	public static boolean isScalarType(Class<?> clazz, String fieldName) {

		try {

			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			Class<?> fieldType = field.getType();

			if (fieldType.getTypeName().equals("java.nio.ByteBuffer")) {
				return true;
			} else if (fieldType.getSuperclass().getTypeName().equals("java.lang.Number")) {
				return true;
			} else if (fieldType.getTypeName().equals("java.lang.String")) {
				return true;
			}

		} catch (NoSuchFieldException e) {
			Exceptions.throwRuntime(e);
		}

		return false;
	}

	private static Object getAttributeValue(AttributeValue value) {
		if (value.getBOOL() != null) {
			return value.getBOOL();
		} else if (value.getNULL() != null) {
			return null;
		} else if (value.getB() != null) {
			return value.getB();
		} else if (value.getBS() != null) {
			return value.getBS();
		} else if (value.getL() != null) {
			return value.getL();
		} else if (value.getM() != null) {
			return value.getM();
		} else if (value.getN() != null) {
			return value.getN();
		} else if (value.getNS() != null) {
			return value.getNS();
		} else if (value.getS() != null) {
			return value.getS();
		} else if (value.getSS() != null) {
			return value.getSS();
		}
		return null;
	}
}
