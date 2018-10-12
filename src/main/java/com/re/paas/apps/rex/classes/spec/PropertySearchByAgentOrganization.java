package com.re.paas.apps.rex.classes.spec;

import com.re.paas.api.listable.ListingFilter;

public class PropertySearchByAgentOrganization extends PropertyListingRequest {

	private Long agentOrganization;

	public Long getAgentOrganization() {
		return agentOrganization;
	}

	public PropertySearchByAgentOrganization setAgentOrganization(Long agentOrganization) {
		this.agentOrganization = agentOrganization;
		return this;
	}

	@Override
	public ListingFilter getListingFilter() {
		
		ListingFilter filter = getDefaultListingFilter()
				.addFilter("agentOrganization", agentOrganization);
	
		return filter;
	}

}
