package com.re.paas.internal.infra.database.tables.definitions;


import com.re.paas.api.infra.database.model.BaseTable;
import com.re.paas.internal.infra.database.tables.attributes.IndexEntrySpec;

public class IndexEntryTable implements BaseTable {

	public String entryId;
	public String indexName;
	public Number partitionId;
	public Number size;
	
	@Override
	public String name() {
		return IndexEntrySpec.TABLE_NAME;
	}

	@Override
	public String hashKey() {
		return IndexEntrySpec.ENTRY_ID;
	}
	
	@Override
	public String rangeKey() {
		return IndexEntrySpec.INDEX_NAME;
	}
}
