package com.re.paas.api.infra.database.document;

import com.re.paas.api.infra.database.model.GlobalSecondaryIndexUpdate;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexUpdate.Action;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.TableUpdate;

public interface GlobalSecondaryIndex extends Index {
	
	default TableDescription delete() {
		
		return getTable().getDatabase()
				.updateTable(new TableUpdate().addGlobalSecondaryIndexUpdates(
						new GlobalSecondaryIndexUpdate().setAction(Action.DELETE)
								.setDefinition(new IndexDefinition(getDescriptor()))));
	}
	
	TableDescription waitForActive();

	TableDescription waitForDelete();
}
