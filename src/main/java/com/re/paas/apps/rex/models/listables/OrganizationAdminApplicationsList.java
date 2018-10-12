package com.re.paas.apps.rex.models.listables;

import java.util.List;

import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.realms.OrganizationAdminRealm;
import com.re.paas.internal.entites.ApplicationEntity;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.models.listables.AbstractApplicationsList;
import com.re.paas.apps.rex.models.listables.IndexedNameTypes;

public class OrganizationAdminApplicationsList extends AbstractApplicationsList {

	private static final List<ListingFilter> DEFAULT_LISTING_FILTERS = new FluentArrayList<ListingFilter>()
			.with(new ListingFilter("role", RoleModel.getDefaultRole(new OrganizationAdminRealm())));

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.ORGANIZATION_ADMIN_APPLICATION;
	}

	@Override
	public boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters) {
		return RoleModel.isAccessAllowed(BaseUserModel.getRole(userId),
				AgentOrganizationFunctionalities.VIEW_ORGANIZATION_ADMIN_APPLICATIONS);
	}

	@Override
	public Class<ApplicationEntity> entityType() {
		return ApplicationEntity.class;
	}

	@Override
	public boolean searchable() {
		return true;
	}

	@Override
	public SearchableUISpec searchableUiSpec() {
		return new SearchableUISpec().setName("agent_organization_applications")
				.setListingPageUrl("/agent-organization-application-search");
	}

	@Override
	public List<ListingFilter> defaultListingFilters() {
		return DEFAULT_LISTING_FILTERS;
	}
}
