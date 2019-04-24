package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.AgentOrganizationWhistleblowMessageTableSpec;

public class AgentOrganizationWhistleblowMessageTable implements BaseTable {

	Long id;

	String agentOrganization;

	Boolean isRead;

	Long userId;

	String name;

	String email;

	String mobile;

	String message;

	Integer resolution;
	
	Map<Integer, Long> resolutionHistory;

	Date dateCreated;

	Date dateUpdated;
	
	@Override
	public String hashKey() {
		return AgentOrganizationWhistleblowMessageTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition resolutionIndex = new IndexDefinition(AgentOrganizationWhistleblowMessageTableSpec.RESOLUTION_INDEX, Type.GSI)
		.addHashKey(AgentOrganizationWhistleblowMessageTableSpec.RESOLUTION);

		indexes.add(resolutionIndex);
		
		return indexes;
	}

}
