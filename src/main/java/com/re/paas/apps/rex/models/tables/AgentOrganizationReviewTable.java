package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.AgentOrganizationReviewTableSpec;

public class AgentOrganizationReviewTable implements BaseTable {

	Long id;
	
	String agentOrganization;

	String userId;
	
	String description;
	
	Integer rating;
	
	Date dateCreated;
	
	@Override
	public String hashKey() {
		return AgentOrganizationReviewTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition agentOrganizationIndex = new IndexDefinition(AgentOrganizationReviewTableSpec.AGENT_ORGANIZATION_INDEX, Type.GSI)
		.addHashKey(AgentOrganizationReviewTableSpec.AGENT_ORGANIZATION);

		IndexDefinition userIdIndex = new IndexDefinition(AgentOrganizationReviewTableSpec.USER_ID_INDEX, Type.GSI)
		.addHashKey(AgentOrganizationReviewTableSpec.USER_ID);
		
		indexes.add(agentOrganizationIndex);
		indexes.add(userIdIndex);
		
		return indexes;
	}
	
}
