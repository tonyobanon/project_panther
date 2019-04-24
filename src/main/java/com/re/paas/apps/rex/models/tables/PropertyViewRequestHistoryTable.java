package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.PropertyViewRequestHistoryTableSpec;

public class PropertyViewRequestHistoryTable implements BaseTable {

	Long id;

	String requestId;

	String agentId;

	Integer status;

	String statusMessage;

	Date dateCreated;

	@Override
	public String hashKey() {
		return PropertyViewRequestHistoryTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition requestIdIndex = new IndexDefinition(PropertyViewRequestHistoryTableSpec.REQUEST_ID_INDEX,
				Type.GSI).addHashKey(PropertyViewRequestHistoryTableSpec.REQUEST_ID);

		indexes.add(requestIdIndex);

		return indexes;
	}

}
