package com.re.paas.internal.infra.database.dynamodb;

import java.util.stream.Stream;

import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.ScanSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.DeleteItemResult;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.PutItemResult;
import com.re.paas.api.infra.database.model.QueryResult;
import com.re.paas.api.infra.database.model.ScanResult;
import com.re.paas.api.infra.database.model.TableDescription;
import com.re.paas.api.infra.database.model.UpdateItemResult;

public class TableImpl implements Table {

	@Override
	public Database getDatabase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableDescription describe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index getIndex(String indexName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PutItemResult putItem(PutItemSpec spec) {
		// getAttributeModel().putValue(getTableName(), item);
		return null;
	}

	@Override
	public Item getItem(GetItemSpec spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UpdateItemResult updateItem(UpdateItemSpec updateItemSpec) {
		// getAttributeModel().updateValue(getTableName(), outcome.getUpdateItemResult().getAttributes(),
		// updateItemSpec.getKeyComponents());
		return null;
	}

	@Override
	public DeleteItemResult deleteItem(DeleteItemSpec spec) {
		// getAttributeModel().deleteValue(getTableName(), spec.getKeyComponents());
		return null;
	}

	@Override
	public Stream<QueryResult> query(QuerySpec spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<ScanResult> scan(ScanSpec spec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Index createGSI(IndexDefinition definition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableDescription waitForActive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TableDescription waitForDelete() {
		// TODO Auto-generated method stub
		return null;
	}

}
