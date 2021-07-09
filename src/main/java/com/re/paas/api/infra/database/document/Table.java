package com.re.paas.api.infra.database.document;

import java.util.stream.Stream;

import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.DeleteItemResult;
import com.re.paas.api.infra.database.model.GlobalSecondaryIndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor;
import com.re.paas.api.infra.database.model.PutItemResult;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.UpdateItemResult;
import com.re.paas.api.infra.database.model.UpdateTableSpec;

public interface Table {
	
	Database getDatabase();
	
	String name();

	TableDescription describe();
	
	Index getIndex(String indexName);

	PutItemResult putItem(PutItemSpec spec);
	
	Item getItem(GetItemSpec spec);

	UpdateItemResult updateItem(UpdateItemSpec updateItemSpec);
	
	DeleteItemResult deleteItem(DeleteItemSpec spec);
	
	QueryResult query(QuerySpec spec);
	
	ScanResult scan(ScanSpec spec);
	
	Index createGSI(GlobalSecondaryIndexDefinition definition);
	
	void deleteGSI(IndexDescriptor index);
	
	TableDescription updateTable(UpdateTableSpec update);
	
	default void delete() {
        getDatabase().deleteTable(name());
    }
	
	TableDescription waitForActive();

	TableDescription waitForDelete();
}
