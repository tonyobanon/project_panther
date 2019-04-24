package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.PropertyViewRequestTableSpec;

public class PropertyViewRequestTable implements BaseTable {

	Long id;

	String userId;
	
	String propertyId;
	
	String agentOrganization;

	String agentId;
	
	Integer status;
	
	String statusMessage;
	
	Date viewingDate;
	
	Date dateCreated;

	Date dateUpdated;
	
	@Override
	public String hashKey() {
		return PropertyViewRequestTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition userIdIndex = new IndexDefinition(PropertyViewRequestTableSpec.USER_ID_INDEX, Type.GSI)
		.addHashKey(PropertyViewRequestTableSpec.USER_ID);

		IndexDefinition propertyIdIndex = new IndexDefinition(PropertyViewRequestTableSpec.PROPERTY_ID_INDEX, Type.GSI)
		.addHashKey(PropertyViewRequestTableSpec.PROPERTY_ID);
		
		IndexDefinition agentOrganizationIndex = new IndexDefinition(PropertyViewRequestTableSpec.AGENT_ORGANIZATION_INDEX, Type.GSI)
				.addHashKey(PropertyViewRequestTableSpec.AGENT_ORGANIZATION)
				.addRangehKey(PropertyViewRequestTableSpec.AGENT_ID);

				
		indexes.add(userIdIndex);
		indexes.add(propertyIdIndex);
		indexes.add(agentOrganizationIndex);
		
		return indexes;
	}
}
