package com.re.paas.internal.tables.defs.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.tables.spec.base.RBEntryTableSpec;

public class RBEntryTable implements BaseTable {

	String key;
	
	String locale;
	
	String value;
	
	Date dateCreated;

	@Override
	public String hashKey() {
		return RBEntryTableSpec.KEY;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition localeIndex = new IndexDefinition(RBEntryTableSpec.LOCALE_INDEX, Type.GSI)
				.setQueryOptimzed(true)
				.addHashKey(RBEntryTableSpec.LOCALE);
		
		indexes.add(localeIndex);
		
		return indexes;
	}
}
