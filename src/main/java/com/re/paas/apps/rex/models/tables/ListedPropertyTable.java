package com.re.paas.apps.rex.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.ListedPropertyTableSpec;

public class ListedPropertyTable implements BaseTable {

	public Long id;

	protected Long propertyId;

	public Long agentOrganization;

	protected Boolean isPremium;

	protected Boolean isHot;

	public Integer availabilityStatus;

	public Integer contractType;

	public Integer paymentOption;

	public Integer yearsRequired;

	protected Date dateCreated;

	protected Date dateUpdated;

	@Override
	public String hashKey() {
		return ListedPropertyTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition agentOrganizationIndex = new IndexDefinition(ListedPropertyTableSpec.AGENT_ORGANIZATION_INDEX,
				Type.GSI).addHashKey(ListedPropertyTableSpec.AGENT_ORGANIZATION);

		IndexDefinition contractTypeIndex = new IndexDefinition(ListedPropertyTableSpec.CONTRACT_TYPE_INDEX, Type.GSI)
				.setQueryOptimzed(true)
				.addHashKey(ListedPropertyTableSpec.CONTRACT_TYPE);

		IndexDefinition paymentOptionIndex = new IndexDefinition(ListedPropertyTableSpec.PAYMENT_OPTION_INDEX, Type.GSI)
				.setQueryOptimzed(true)
				.addHashKey(ListedPropertyTableSpec.PAYMENT_OPTION);

		IndexDefinition yearsRequiredIndex = new IndexDefinition(ListedPropertyTableSpec.YEARS_REQUIRED_INDEX, Type.GSI)
				.setQueryOptimzed(true)
				.addHashKey(ListedPropertyTableSpec.YEARS_REQUIRED);

		indexes.add(agentOrganizationIndex);
		indexes.add(contractTypeIndex);
		indexes.add(paymentOptionIndex);
		indexes.add(yearsRequiredIndex);

		return indexes;
	}
}
