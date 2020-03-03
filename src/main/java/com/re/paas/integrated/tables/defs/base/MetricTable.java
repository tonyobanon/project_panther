package com.re.paas.integrated.tables.defs.base;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.base.MetricTableSpec;

public class MetricTable implements BaseTable {

	String key;
	String parentKey;
	Integer value;

	@Override
	public String hashKey() {
		return MetricTableSpec.KEY;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition parentKeyIndex = new IndexDefinition(MetricTableSpec.PARENT_KEY_INDEX, Type.GSI)
		.addHashKey(MetricTableSpec.PARENT_KEY);

		indexes.add(parentKeyIndex);
		
		return indexes;
	}
}
