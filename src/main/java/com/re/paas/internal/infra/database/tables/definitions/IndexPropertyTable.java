package com.re.paas.internal.infra.database.tables.definitions;

import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.internal.infra.database.tables.attributes.IndexPropertySpec;

public class IndexPropertyTable implements BaseTable {

	public String id;
	public String hashKey;
	public String rangeKey;
	public String projections;
	public Long provisionedThroughput;
	public String tableHashKey;
	public String tableRangeKey;
	public String queryType;

	@Override
	public String name() {
		return IndexPropertySpec.TABLE_NAME;
	}
	
	@Override
	public String hashKey() {
		return IndexPropertySpec.ID;
	}
}
