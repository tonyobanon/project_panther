package com.re.paas.integrated.infra.database.dynamodb.qopt.tables;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.infra.database.dynamodb.qopt.attributes.IndexPropertySpec;

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
	public String hashKey() {
		return IndexPropertySpec.ID;
	}
}
