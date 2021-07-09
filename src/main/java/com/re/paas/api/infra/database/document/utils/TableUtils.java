package com.re.paas.api.infra.database.document.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.database.model.AttributeDefinition;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.KeySchemaElement;
import com.re.paas.api.infra.database.model.KeyType;
import com.re.paas.api.infra.database.model.LocalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.TableDefinition;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.ClassUtils;

public class TableUtils {

	private static final Logger LOG = Logger.get(Logger.class);

	public static TableDefinition toTableDefinition(Class<? extends BaseTable> entity) {

		BaseTable table = ClassUtils.createInstance(entity);

		if (!table.enabled()) {
			LOG.info("Skipping table '" + entity.getSimpleName() + "'");
			return null;
		}

		if (table.name().contains("/")) {
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
		
	
		addAttribute.accept(new AttributeDefinition(table.hashKey(), ItemUtils.getScalarType(entity, table.hashKey())));

		if (table.rangeKey() != null) {
			addAttribute.accept(new AttributeDefinition(table.rangeKey(), ItemUtils.getScalarType(entity, table.rangeKey())));
		}

		
		Collection<KeySchemaElement> primaryKeys = new ArrayList<>();

		primaryKeys.add(new KeySchemaElement(table.hashKey(), KeyType.HASH));

		if (table.rangeKey() != null) {
			primaryKeys.add(new KeySchemaElement(table.rangeKey(), KeyType.RANGE));
		}

		def.setKeySchema(primaryKeys);


		List<LocalSecondaryIndexDefinition> localSecondaryIndexes = new ArrayList<LocalSecondaryIndexDefinition>();

		for (LocalSecondaryIndexDefinition index : table.localSecondaryIndexes()) {

			String rangeKey = ItemUtils.getSchemaKey(index.getKeySchema(), KeyType.RANGE);

			if (rangeKey == null) {
				throw new RuntimeException("Empty rangeKey for index: " + index.getIndexName());
			}

			localSecondaryIndexes.add(index);

			addAttribute.accept(new AttributeDefinition(rangeKey, ItemUtils.getScalarType(entity, rangeKey)));	
		}

		if (localSecondaryIndexes.size() > 0) {
			def.setLocalSecondaryIndexes(localSecondaryIndexes);
		}

		List<GlobalSecondaryIndexDefinition> globalSecondaryIndexes = new ArrayList<GlobalSecondaryIndexDefinition>();

		for (GlobalSecondaryIndexDefinition index : table.globalSecondaryIndexes()) {

			String hashKey = ItemUtils.getSchemaKey(index.getKeySchema(), KeyType.HASH);
			String rangeKey = ItemUtils.getSchemaKey(index.getKeySchema(), KeyType.RANGE);

			if (hashKey == null) {
				throw new RuntimeException("Empty hashKey for index: " + index.getIndexName());
			}

			if (index.forTextSearch()) {

				if (!ItemUtils.isNumber(entity, hashKey)) {
					throw new RuntimeException(
							table.name() + ": Only Number-Typed hash attributes are allowed for this GSI");
				}

				if (index.getQueryType() == null) {
					throw new RuntimeException(
							table.name() + "/" + index.getIndexName() + ": " + "A query type must be provided");
				}
			}

			globalSecondaryIndexes.add(index);

			addAttribute.accept(new AttributeDefinition(hashKey, ItemUtils.getScalarType(entity, hashKey)));

			if (rangeKey != null) {
				addAttribute.accept(new AttributeDefinition(rangeKey, ItemUtils.getScalarType(entity, rangeKey)));
			}
		}

		if (globalSecondaryIndexes.size() > 0) {
			def.setGlobalSecondaryIndexes(globalSecondaryIndexes);
		}

		def.setAttributeDefinitions(attributeDefinitions);

		return def;
	}

}
