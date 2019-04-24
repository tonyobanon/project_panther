package com.re.paas.internal.infra.database.dynamodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.kylantis.eaa.core.attributes.BaseEntitySpec;
import com.kylantis.eaa.core.base.StorageServiceFactory;
import com.kylantis.eaa.core.indexing.tools.QueryModel;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.TableDefinition;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.ClassIdentityType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.classes.ClasspathScanner;
import com.re.paas.internal.infra.database.dynamodb.utils.DBTooling;

public class CreateTableHelper {

	private static final Logger LOG = Logger.get(Logger.class);
	
	//private static final ProvisionedThroughput DEFAULT_PROVISIONED_THROUGHPUT = new ProvisionedThroughput(3000l, 1000l);
	private static final ProvisionedThroughput DEFAULT_PROVISIONED_THROUGHPUT = new ProvisionedThroughput(100l, 50l);
		
	public static void createTable(TableDefinition instance) {

			LOG.info("Creating table '" + instance.getTableName() + "'");

			CreateTableRequest CTR = new CreateTableRequest();
			CTR.setTableName(BaseEntitySpec.getPrefix() + EntityModeller.getTableNameDelimiter() + entity.getSimpleName());
			CTR.setProvisionedThroughput(DEFAULT_PROVISIONED_THROUGHPUT);

			Collection<KeySchemaElement> primaryKeys = new ArrayList<>();
			primaryKeys.add(new KeySchemaElement(instance.tableHashKey(), KeyType.HASH));
			if (instance.tableRangeKey() != null) {
				primaryKeys.add(new KeySchemaElement(instance.tableRangeKey(), KeyType.RANGE));
			}
			CTR.setKeySchema(primaryKeys);

			List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

			LocalSecondaryIndex
			
			attributeDefinitions.add(new AttributeDefinition(instance.tableHashKey(),
					DBTooling.getScalarType(entity, instance.tableHashKey())));
			if (instance.tableRangeKey() != null) {
				attributeDefinitions.add(new AttributeDefinition(instance.tableRangeKey(),
						DBTooling.getScalarType(entity, instance.tableRangeKey())));
			}

			List<LocalSecondaryIndex> localSecondaryIndexes = new ArrayList<LocalSecondaryIndex>(
					instance.LSIs().size());

			List<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<GlobalSecondaryIndex>(
					instance.indexedGSIs().size() + instance.unindexedGSIs().size());

			
			for(instance.in) {
				
			}
			
			
			for (Entry<String, IndexConfig> LSI : instance.LSIs().entrySet()) {

				IndexConfig index = LSI.getValue();

			}

			for (Entry<String, IndexConfig> unindexedGSI : instance.unindexedGSIs().entrySet()) {

				IndexConfig index = unindexedGSI.getValue();

				globalSecondaryIndexes.add(new GlobalSecondaryIndex().withIndexName(unindexedGSI.getKey())
						.withKeySchema(index.getKeySchemaElement()).withProjection(index.getProjection())
						.withProvisionedThroughput(index.getProvisionedThroughput()));

				for (KeySchemaElement keySchema : index.getKeySchemaElement()) {
					attributeDefinitions.add(new AttributeDefinition(keySchema.getAttributeName(),
							DBTooling.getScalarType(entity, keySchema.getAttributeName())));
				}
			}

			for (Entry<String, IndexConfig> indexedGSI : instance.indexedGSIs().entrySet()) {
				IndexConfig index = indexedGSI.getValue();

				// Verify Index keys

				if (!DBTooling.isNumber(entity, index.getKeySchemaElement()[0].getAttributeName())) {
					throw new RuntimeException("Only Number-Typed hash attributes are allowed for this GSI");
				}

				if (!DBTooling.isScalarType(entity, index.getKeySchemaElement()[1].getAttributeName())) {
					throw new RuntimeException("Only Scalar-Typed range attributes are allowed for GSIs");
				}

				globalSecondaryIndexes.add(new GlobalSecondaryIndex().withIndexName(indexedGSI.getKey())
						.withKeySchema(index.getKeySchemaElement()).withProjection(index.getProjection())
						.withProvisionedThroughput(index.getProvisionedThroughput()));

				for (KeySchemaElement keySchema : index.getKeySchemaElement()) {
					attributeDefinitions.add(new AttributeDefinition(keySchema.getAttributeName(),
							DBTooling.getScalarType(entity, keySchema.getAttributeName())));
				}

				// Get table hash attribute

				String tableHashKey = null;
				String tableRangeKey = null;

				for (KeySchemaElement e : CTR.getKeySchema()) {
					if (e.getKeyType().equals("HASH")) {
						tableHashKey = e.getAttributeName();
					} else {
						tableRangeKey = e.getAttributeName();
					}
				}

				new QueryModel().newQueryOptimizedGSI(new IndexDescriptor(CTR.getTableName(), indexedGSI.getKey()),
						index.getProjection().getNonKeyAttributes(), index.getQueryType(),
						index.getKeySchemaElement()[0].getAttributeName(),
						index.getKeySchemaElement()[1].getAttributeName(),
						index.getProvisionedThroughput().getReadCapacityUnits(), tableHashKey, tableRangeKey);
			}

			if (globalSecondaryIndexes.size() > 0) {
				CTR.setGlobalSecondaryIndexes(globalSecondaryIndexes);
			}

			if (localSecondaryIndexes.size() > 0) {
				CTR.setLocalSecondaryIndexes(localSecondaryIndexes);
			}

			CTR.setAttributeDefinitions(attributeDefinitions);

			try {
				StorageServiceFactory.getDocumentDatabase().createTable(CTR).waitForActive();
			} catch (InterruptedException e) {
				// Continue
			}

	}
	
	private static void createLSI(CreateTableRequest CTR, IndexDefinition lsi) {

		localSecondaryIndexes.add(new LocalSecondaryIndex().withIndexName(LSI.getKey())
				.withKeySchema(index.getKeySchemaElement()).withProjection(index.getProjection()));

		attributeDefinitions.add(new AttributeDefinition(index.getKeySchemaElement()[1].getAttributeName(),
				DBTooling.getScalarType(entity, index.getKeySchemaElement()[1].getAttributeName())));
		
		
	}
	
	private static void createGSI(IndexDefinition gsi) {
		
	}

	private static void createQueryOptimizedGSI(IndexDefinition gsi) {
		
	}

	public static String getTableNameDelimiter() {
		return "_";
	}

	private static KeySchemaElement[] getKeySchemaElement(com.re.paas.api.infra.database.model.KeySchemaElement... e) {
		KeySchemaElement result[] = new KeySchemaElement[e.length];

		for (int i = 0; i < e.length; i++) {
			result[i] = new KeySchemaElement(e[i].getAttributeName(), e[i].getKeyType());
		}

		return result;
	}
	
	private static Projection getProjection(com.re.paas.api.infra.database.model.Projection p) {
		return new Projection().withProjectionType(p.getProjectionType()).withNonKeyAttributes(p.getNonKeyAttributes());
	}
	
	private static ProjectionType getProjection(com.re.paas.api.infra.database.model.ProjectionType p) {
		return ProjectionType.fromValue(p.toString());
	}

}
