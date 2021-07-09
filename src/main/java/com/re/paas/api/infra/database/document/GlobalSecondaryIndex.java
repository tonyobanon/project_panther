package com.re.paas.api.infra.database.document;

import com.re.paas.api.infra.database.model.TableDescription;

public interface GlobalSecondaryIndex extends Index {
	
	default void delete() {
		getTable().deleteGSI(getDescriptor());
	}
	
	TableDescription waitForActive();

	TableDescription waitForDelete();
}
