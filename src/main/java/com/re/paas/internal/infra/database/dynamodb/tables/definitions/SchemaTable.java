package com.re.paas.internal.infra.database.dynamodb.tables.definitions;

import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.internal.infra.database.dynamodb.tables.attributes.SchemaSpec;

public class SchemaTable implements BaseTable {

	public String path;
	public String attributes;
	public String hashKey;
	public String rangeKey;

	@Override
	public String name() {
		return SchemaSpec.TABLE_NAME;
	}
	
	@Override
	public String hashKey() {
		return SchemaSpec.PATH;
	}
}
