package com.re.paas.api.infra.database.document;

import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDescription;

public interface GlobalSecondaryIndex extends Index {
	
	GlobalSecondaryIndex delete();
	
	/**
	 * This does not make a network call. To fetch the latest information, call {@link Table#describe()}
	 */
	@Override
	GlobalSecondaryIndexDescription getDescriptor();
	
	GlobalSecondaryIndex waitForActive();

	GlobalSecondaryIndex waitForDelete();
	
}
