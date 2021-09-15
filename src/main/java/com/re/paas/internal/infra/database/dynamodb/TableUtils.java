package com.re.paas.internal.infra.database.dynamodb;

import static com.re.paas.internal.infra.database.dynamodb.classes.DynamoDBConstants.AttributeTypes.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.database.model.AttributeDefinition;
import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.KeySchemaElement;
import com.re.paas.api.infra.database.model.KeyType;
import com.re.paas.api.infra.database.model.LocalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.TableDefinition;
import com.re.paas.api.infra.database.model.exceptions.AttributeNotFoundException;
import com.re.paas.api.infra.database.model.exceptions.IndexAlreadyExistsException;
import com.re.paas.api.infra.database.model.exceptions.UnknownAttributeTypeException;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Collections;

class TableUtils {

	public static final String reservedDelimeter = "/";
	private static final Logger LOG = Logger.get(Logger.class);

	public static String getScalarAttribute(Map<String, String> schema, String name) {

		var scalarTypes = List.of(S, N, B, BOOL);
		String type = schema.get(name);

		if (type == null) {
			throw new AttributeNotFoundException(name);
		}

		if (!scalarTypes.contains(type)) {
			throw new UnknownAttributeTypeException(name, type);
		}

		return type;
	}

	public static TableDefinition toTableDefinition(Map<String, String> schema, Class<? extends BaseTable> entity) {

		BaseTable table = ClassUtils.createInstance(entity);

		if (!table.enabled()) {
			LOG.info("Skipping table '" + entity.getSimpleName() + "'");
			return null;
		}

		if (table.name().contains(reservedDelimeter)) {
			Exceptions.throwRuntime("Invalid table name: " + table.name());
		}

		LOG.info("Creating table '" + entity.getSimpleName() + "'");

		TableDefinition def = new TableDefinition(table.name());

		List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

		Consumer<AttributeDefinition> addAttribute = (attr) -> {

			// If already exists, return
			for (AttributeDefinition a : attributeDefinitions) {
				if (a.getAttributeName().equals(attr.getAttributeName())) {
					return;
				}
			}

			attributeDefinitions.add(attr);
		};

		addAttribute.accept(new AttributeDefinition(table.hashKey(), getScalarAttribute(schema, table.hashKey())));

		if (table.rangeKey() != null) {
			addAttribute
					.accept(new AttributeDefinition(table.rangeKey(), getScalarAttribute(schema, table.rangeKey())));
		}

		Collection<KeySchemaElement> primaryKeys = new ArrayList<>();

		primaryKeys.add(new KeySchemaElement(table.hashKey(), KeyType.HASH));

		if (table.rangeKey() != null) {
			primaryKeys.add(new KeySchemaElement(table.rangeKey(), KeyType.RANGE));
		}

		def.setKeySchema(primaryKeys);

		List<String> indexNames = new ArrayList<>();

		List<LocalSecondaryIndexDefinition> localSecondaryIndexes = new ArrayList<LocalSecondaryIndexDefinition>();

		for (LocalSecondaryIndexDefinition index : table.localSecondaryIndexes()) {

			if (index.getIndexName().contains(reservedDelimeter)) {
				Exceptions.throwRuntime("Invalid index name: " + index.getIndexName());
			}

			if (indexNames.contains(index.getIndexName())) {
				throw new IndexAlreadyExistsException(index.getIndexName());
			}

			String rangeKey = KeySchemaElement.getRangeKey(index.getKeySchema());

			if (rangeKey == null) {
				throw new RuntimeException("Empty rangeKey for index: " + index.getIndexName());
			}

			localSecondaryIndexes.add(index);

			addAttribute.accept(new AttributeDefinition(rangeKey, getScalarAttribute(schema, rangeKey)));

			indexNames.add(index.getIndexName());
		}

		if (localSecondaryIndexes.size() > 0) {
			def.setLocalSecondaryIndexes(localSecondaryIndexes);
		}

		List<GlobalSecondaryIndexDefinition> globalSecondaryIndexes = new ArrayList<GlobalSecondaryIndexDefinition>();

		for (GlobalSecondaryIndexDefinition index : table.globalSecondaryIndexes()) {

			if (index.getIndexName().contains(reservedDelimeter)) {
				Exceptions.throwRuntime("Invalid index name: " + index.getIndexName());
			}

			if (indexNames.contains(index.getIndexName())) {
				throw new IndexAlreadyExistsException(index.getIndexName());
			}

			String hashKey = KeySchemaElement.getHashKey(index.getKeySchema());
			String rangeKey = KeySchemaElement.getRangeKey(index.getKeySchema());

			if (hashKey == null) {
				throw new RuntimeException("Empty hashKey for index: " + index.getIndexName());
			}

			globalSecondaryIndexes.add(index);

			addAttribute.accept(new AttributeDefinition(hashKey, getScalarAttribute(schema, hashKey)));

			if (rangeKey != null) {
				addAttribute.accept(new AttributeDefinition(rangeKey, getScalarAttribute(schema, rangeKey)));
			}

			if (index.forTextSearch()) {

				if (!schema.get(hashKey).equals(N)) {
					throw new RuntimeException(
							table.name() + ": Only Number-Typed hash attributes are allowed for this GSI");
				}

				if (rangeKey == null) {
					throw new RuntimeException(
							table.name() + ": Range key is required for GSI: " + index.getIndexName());
				}

				if (index.getQueryType() == null) {
					throw new RuntimeException(
							table.name() + "/" + index.getIndexName() + ": " + "A query type must be provided");
				}
			}

			indexNames.add(index.getIndexName());
		}

		List<String> textSearchRangeKeys = table.globalSecondaryIndexes().stream().filter(gsi -> gsi.forTextSearch())
				.map(gsi -> KeySchemaElement.getRangeKey(gsi.getKeySchema())).collect(Collectors.toList());

		if (!Collections.hasUniqueElements(textSearchRangeKeys)) {
			
			// Hence, it is imperative that no two text search GSI has the same range key
			throw new RuntimeException(table.name() + ": All text-search GSIs must have unique rangeKeys");
		}

		if (globalSecondaryIndexes.size() > 0) {
			def.setGlobalSecondaryIndexes(globalSecondaryIndexes);
		}

		def.setAttributeDefinitions(attributeDefinitions);

		return def;
	}

}
