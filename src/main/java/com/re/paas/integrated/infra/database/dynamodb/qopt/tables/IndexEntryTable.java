package com.re.paas.integrated.infra.database.dynamodb.qopt.tables;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.infra.database.dynamodb.qopt.attributes.IndexEntrySpec;

public class IndexEntryTable implements BaseTable {

	public String entryId;
	public String indexName;
	public int partitionId;
	public int size;


	@Override
	public String hashKey() {
		return IndexEntrySpec.ENTRY_ID;
	}
	
	@Override
	public String rangeKey() {
		return IndexEntrySpec.INDEX_NAME;
	}
}
