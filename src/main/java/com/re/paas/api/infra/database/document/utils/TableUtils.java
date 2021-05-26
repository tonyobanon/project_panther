package com.re.paas.api.infra.database.document.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.model.AttributeDefinition;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDescription;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.model.KeySchemaElement;
import com.re.paas.api.infra.database.model.KeyType;
import com.re.paas.api.infra.database.model.LocalSecondaryIndexDescription;
import com.re.paas.api.infra.database.model.ProvisionedThroughput;
import com.re.paas.api.infra.database.model.TableDefinition;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.ClassUtils;

public class TableUtils {

	private static final Logger LOG = Logger.get(Logger.class);

	private static ProvisionedThroughput defaultProvisionedthroughput = new ProvisionedThroughput(3000l, 1000l);


	public static Boolean createTable(Class<? extends BaseTable> entity) {

		BaseTable table = ClassUtils.createInstance(entity);

		if (!table.enabled()) {
			LOG.info("Skipping table '" + entity.getSimpleName() + "'");
			return false;
		}

		LOG.info("Creating table '" + entity.getSimpleName() + "'");

		
		TableDefinition request = new TableDefinition();
		
		request.setTableName(table.name());
		
		request.setProvisionedThroughput(defaultProvisionedthroughput);

		
		Collection<KeySchemaElement> primaryKeys = new ArrayList<>();

		primaryKeys.add(new KeySchemaElement(table.hashKey(), KeyType.HASH));

		if (table.rangeKey() != null) {
			primaryKeys.add(new KeySchemaElement(table.rangeKey(), KeyType.RANGE));
		}
		request.setKeySchema(primaryKeys);

		List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

		
		attributeDefinitions
				.add(new AttributeDefinition(table.hashKey(), ItemUtils.getScalarType(entity, table.hashKey())));

		if (table.rangeKey() != null) {
			attributeDefinitions
					.add(new AttributeDefinition(table.rangeKey(), ItemUtils.getScalarType(entity, table.rangeKey())));
		}

		List<IndexDefinition> localSecondaryIndexes = new ArrayList<IndexDefinition>();

		List<IndexDefinition> globalSecondaryIndexes = new ArrayList<IndexDefinition>();

		for (IndexDefinition index : table.indexes().stream().filter(i -> i.getType() == Type.LSI)
				.collect(Collectors.toList())) {

			if (index.getKeySchema().size() > 1) {
				Exceptions.throwRuntime(
						entity.getName() + ": Index '" + index.getIndexName() + "' should have 1 KeySchemaElement");
			}

			localSecondaryIndexes.add(new LocalSecondaryIndexDescription(request.getTableName(), index.getIndexName())
					.withKeySchema(index.getKeySchema()).withProjection(index.getProjection()));

			attributeDefinitions.add(new AttributeDefinition(index.getKeySchema().get(0).getAttributeName(),
					ItemUtils.getScalarType(entity, index.getKeySchema().get(0).getAttributeName())));
		}

		for (IndexDefinition index : table.indexes().stream().filter(i -> i.getType() == Type.GSI)
				.collect(Collectors.toList())) {

			String hashKey = ItemUtils.getSchemaKey(index.getKeySchema(), KeyType.HASH);
			String rangeKey = ItemUtils.getSchemaKey(index.getKeySchema(), KeyType.RANGE);


			if (index.isQueryOptimzed() && !ItemUtils.isNumber(entity, hashKey)) {
				throw new RuntimeException("Only Number-Typed hash attributes are allowed for this GSI");
			}

			if (rangeKey != null && !ItemUtils.isScalarType(entity, rangeKey)) {
				throw new RuntimeException("Only Scalar-Typed range attributes are allowed for GSIs");
			}
			


			globalSecondaryIndexes.add(
					new GlobalSecondaryIndexDescription(request.getTableName(), index.getIndexName()).addHashKey(hashKey)
							.addRangeKey(rangeKey).withProjection(index.getProjection()).setProvisionedThroughput(
									index.getProvisionedThroughput() != null ? index.getProvisionedThroughput()
											: defaultProvisionedthroughput));

			attributeDefinitions.add(new AttributeDefinition(hashKey, ItemUtils.getScalarType(entity, hashKey)));

			if (rangeKey != null) {
				attributeDefinitions.add(new AttributeDefinition(rangeKey, ItemUtils.getScalarType(entity, rangeKey)));
			}
			
			Database.get().getTextSearch().getQueryModel().newQueryOptimizedGSI(new IndexDescriptor(request.getTableName(), index.getIndexName()),
					index.getProjection().getNonKeyAttributes(), index.getQueryType(), hashKey, rangeKey,
					index.getProvisionedThroughput().getReadCapacityUnits(), table.hashKey(), table.rangeKey());
		}

		if (globalSecondaryIndexes.size() > 0) {
			request.setGlobalSecondaryIndexes(globalSecondaryIndexes);
		}

		if (localSecondaryIndexes.size() > 0) {
			request.setLocalSecondaryIndexes(localSecondaryIndexes);
		}

		request.setAttributeDefinitions(attributeDefinitions);

		Database.get().createTable(request).waitForActive();

		return true;
	}
	
	public static String getTableNameDelimiter() {
		return "_";
	}

}
