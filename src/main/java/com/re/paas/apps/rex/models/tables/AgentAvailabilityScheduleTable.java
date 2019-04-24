package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.AgentAvailabilityScheduleTableSpec;

public class AgentAvailabilityScheduleTable implements BaseTable {

	String id;
	String agent;
	Map<String, String> baseSchedules;
	Date dateUpdated;
	
	@Override
	public String hashKey() {
		return AgentAvailabilityScheduleTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition sizeIndex = new IndexDefinition(AgentAvailabilityScheduleTableSpec.AGENT_INDEX, Type.GSI)
		.addHashKey(AgentAvailabilityScheduleTableSpec.AGENT);
		
		indexes.add(sizeIndex);
		
		return indexes;
	}

}
