package com.re.paas.internal.models.listables;

import java.util.List;

import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.internal.fusion.functionalities.UserApplicationFunctionalities;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.realms.AdminRealm;
import com.re.paas.internal.tables.defs.users.ApplicationTable;

public class AdminApplicationsList extends AbstractApplicationsList {

	private static final List<ListingFilter> DEFAULT_LISTING_FILTERS = new FluentArrayList<ListingFilter>()
			.with(new ListingFilter("role", RoleModel.getDefaultRole(new AdminRealm())));

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.ADMIN_APPLICATION;
	}

	@Override
	public boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters) {
		return RoleModel.isAccessAllowed(BaseUserModel.getRole(userId), UserApplicationFunctionalities.VIEW_ADMIN_APPLICATIONS);
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
		return new SearchableUISpec().setName("admin_applications").setListingPageUrl("/admin-application-search");
	}

	@Override
	public List<ListingFilter> defaultListingFilters() {
		return DEFAULT_LISTING_FILTERS;
	}
}
