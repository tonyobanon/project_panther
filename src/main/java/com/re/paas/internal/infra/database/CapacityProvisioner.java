package com.re.paas.internal.infra.database;

import com.re.paas.api.infra.database.Namespace;
import com.re.paas.api.infra.database.model.CapacityUnits;

public class CapacityProvisioner {

	static void create(Namespace namespace, Boolean provisionReads) {

		// The table/index has been provisioned with the default capacity
		
		
		// Create a job that runs every 1 second to capture units consumed by each namespace
		// every second, and then continually increase/decrease read/write units as needed
		
		// If !provisionReads, we will never update the read units (as is the case for TSB indexes)
		
		
	}

	static CapacityUnits defaultTableCapacity() {
		// TODO Auto-generated method stub
		return null;
	}

	static CapacityUnits defaultGsiCapacity() {
		// TODO Auto-generated method stub
		return null;
	}

	static void consume(Namespace namespace, CapacityUnits units) {
		
	}
}
