package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.PropertyFeaturesTableSpec;


public class PropertyFeaturesTable implements BaseTable {

	Long id;
	Integer type;
	ClientRBRef title;
	Date dateCreated;

	@Override
	public String hashKey() {
		return PropertyFeaturesTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition agentOrganizationIndex = new IndexDefinition(PropertyFeaturesTableSpec.TYPE_INDEX, Type.GSI)
		.addHashKey(PropertyFeaturesTableSpec.TYPE);

		indexes.add(agentOrganizationIndex);
		
		return indexes;
	}

}
