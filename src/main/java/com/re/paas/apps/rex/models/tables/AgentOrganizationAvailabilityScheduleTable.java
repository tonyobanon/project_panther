package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.AgentOrganizationAvailabilityScheduleTableSpec;

public class AgentOrganizationAvailabilityScheduleTable implements BaseTable {

	String id;
	String agentOrganization;
	Map<String, String> baseSchedules;
	Date dateUpdated;

	@Override
	public String hashKey() {
		return AgentOrganizationAvailabilityScheduleTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition sizeIndex = new IndexDefinition(AgentOrganizationAvailabilityScheduleTableSpec.AGENT_ORGANIZATION_INDEX, Type.GSI)
		.addHashKey(AgentOrganizationAvailabilityScheduleTableSpec.AGENT_ORGANIZATION);
		
		indexes.add(sizeIndex);
		
		return indexes;
	}
}
