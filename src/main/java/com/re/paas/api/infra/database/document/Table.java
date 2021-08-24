package com.re.paas.api.infra.database.document;

import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.api.infra.database.model.DeleteItemResult;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.PutItemResult;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.UpdateItemResult;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.api.tasks.RequiresAffinity;

public interface Table {

	Database getDatabase();

	String name();

	default TableDescription describe() {
		return describe(true);
	}
	
	TableDescription describe(boolean loadTextSearchInfo);

	Index getIndex(String indexName);

	PutItemResult putItem(PutItemSpec spec);

	default PutItemResult putItem(Item item) {
		return putItem(new ExpressionSpecBuilder().buildForPut().withItem(item));
	}

	Item getItem(GetItemSpec spec);

	default Item getItem(PrimaryKey key) {
		return getItem(new ExpressionSpecBuilder().buildForGetItem().setPrimaryKey(key));
	}

	UpdateItemResult updateItem(UpdateItemSpec updateItemSpec);

	DeleteItemResult deleteItem(DeleteItemSpec spec);

	QueryResult query(QuerySpec spec);

	ScanResult scan(ScanSpec spec);

	GlobalSecondaryIndex createGSI(GlobalSecondaryIndexDefinition definition);

	@RequiresAffinity(Affinity.EACH)
	void refreshSchema(Class<? extends BaseTable> model);

	Table delete();
	
	Table waitForActive();

	Table waitForDelete();
}
