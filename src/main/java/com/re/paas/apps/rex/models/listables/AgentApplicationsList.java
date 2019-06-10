package com.re.paas.apps.rex.models.listables;

import java.util.List;

import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.apps.rex.functionality.AgentFunctionalities;
import com.re.paas.apps.rex.realms.AgentRealm;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.models.listables.AbstractApplicationsList;
import com.re.paas.internal.tables.defs.users.ApplicationTable;

public class AgentApplicationsList extends AbstractApplicationsList {

	private static final List<ListingFilter> DEFAULT_LISTING_FILTERS = new FluentArrayList<ListingFilter>()
			.with(new ListingFilter("role", RoleModel.getDefaultRole(new AgentRealm())));

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.AGENT_APPLICATION;
	}

	@Override
	public boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters) {
		return 
				RoleModel.isAccessAllowed(BaseUserModel.getRole(userId), AgentFunctionalities.VIEW_AGENT_APPLICATIONS)
				&&
				AbstractAgentOrganizationList.canProvisionOrganization(userId, listingFilters);
	}

	@Override
	public Class<ApplicationTable> entityType() {
		return ApplicationTable.class;
	}

	@Override
	public boolean searchable() {
		return true;
	}

	@Override
	public SearchableUISpec searchableUiSpec() {
		return new SearchableUISpec().setName("agent_applications").setListingPageUrl("/agent-application-search");
	}

	@Override
	public List<ListingFilter> defaultListingFilters() {
		return DEFAULT_LISTING_FILTERS;
	}
}
