package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.AgentOrganizationTableSpec;

public class AgentOrganizationTable implements BaseTable {

	Long id;
	String name;
	Long phone;
	String email;
	String logo;
	
	Long admin;
	List<Long> agents;
	Integer rating;
	
	String address;
	Integer postalCode;
	
	Integer city;
	String territory;
	String country;

	@Override
	public String hashKey() {
		return AgentOrganizationTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition agentOrganizationIndex = new IndexDefinition(AgentOrganizationTableSpec.TERRITORY_INDEX, Type.GSI)
		.addHashKey(AgentOrganizationTableSpec.TERRITORY);
		
		indexes.add(agentOrganizationIndex);
		
		return indexes;
	}
}
