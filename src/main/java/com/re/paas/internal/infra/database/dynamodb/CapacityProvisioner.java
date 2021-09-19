package com.re.paas.internal.infra.database.dynamodb;

import com.amazonaws.services.dynamodbv2.model.ConsumedCapacity;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.infra.database.model.CapacityUnits;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.internal.infra.database.dynamodb.classes.Namespace;

public class CapacityProvisioner {

	private static CapacityUnits defaultTableCapacity() {
		// TODO Auto-generated method stub
		return null;
	}

	private static CapacityUnits defaultGsiCapacity() {
		// TODO Auto-generated method stub
		return null;
	}

	@BlockerTodo("Determine a way to prune previously created capacity. What if the index creation failed?")
	static void create(Namespace namespace, Boolean provisionReads) {

		// The table/index has been provisioned with the default capacity

		// Create a job that runs every 1 second to capture units consumed by each
		// namespace
		// every second, and then continually increase/decrease read/write units as
		// needed

		// If !provisionReads, we will never update the read units (as is the case for
		// TSB indexes)
	}

	static CapacityUnits createTableCapacity(String tableName) {

		CapacityUnits capacity = defaultTableCapacity();

		CapacityProvisioner.create(Namespace.from(tableName), true);

		return capacity;
	}

	static CapacityUnits createGsiCapacity(String tableName, GlobalSecondaryIndexDefinition definition) {

		CapacityUnits capacity = defaultGsiCapacity();

		CapacityProvisioner.create(Namespace.from(tableName, definition.getIndexName()),

				// For textsearch-based indexes, we do not provision reads because of the
				// advanced optimizations we perform throughout the life-cycle of the index.

				!definition.forTextSearch());

		return capacity;
	}
	
	@BlockerTodo
	static void consumeRead(Namespace ns, ConsumedCapacity consumedCapacity) {
		
		assert consumedCapacity.getWriteCapacityUnits() == 0;
		
	}

	@BlockerTodo
	static void consumeWrite(Namespace ns, ConsumedCapacity consumedCapacity) {

		assert consumedCapacity.getReadCapacityUnits() == 0;
		
		// For Table
		// consumedCapacity.getTableName()
		// consumedCapacity.getTable()

		// For LSIs
		// consumedCapacity.getLocalSecondaryIndexes().forEach((indexName, capacity) -> {
		// });

		// For GSIs
		// consumedCapacity.getLocalSecondaryIndexes().forEach((indexName, capacity) -> {
		// });
	}

}
