package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.AgentOrganizationMessageTableSpec;

public class AgentOrganizationMessageTable implements BaseTable {

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
		return AgentOrganizationMessageTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition agentOrganizationIndex = new IndexDefinition(
				AgentOrganizationMessageTableSpec.AGENT_ORGANIZATION_INDEX, Type.GSI)
						.addHashKey(AgentOrganizationMessageTableSpec.AGENT_ORGANIZATION)
						.addRangehKey(AgentOrganizationMessageTableSpec.RESOLUTION);

		indexes.add(agentOrganizationIndex);

		return indexes;
	}

}
