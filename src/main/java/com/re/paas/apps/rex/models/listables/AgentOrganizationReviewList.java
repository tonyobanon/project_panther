package com.re.paas.apps.rex.models.listables;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationReviewSpec;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.models.tables.AgentOrganizationReviewTable;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.models.helpers.EntityHelper;

public class AgentOrganizationReviewList extends Listable<AgentOrganizationReviewSpec> {

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.AGENT_ORGANIZATION_REVIEW;
	}

	@Override
	public boolean authenticate(ListingType type, Long principal, List<ListingFilter> listingFilters) {

		String role = BaseUserModel.getRole(principal);
		return RoleModel.isAccessAllowed(role, AgentOrganizationFunctionalities.VIEW_AGENT_ORGANIZATION_REVIEWS);
	}

	@Override
	public Class<AgentOrganizationReviewTable> entityType() {
		return AgentOrganizationReviewTable.class;
	}

	@Override
	public boolean searchable() {
		return false;
	}

	@Override
	public Map<Long, AgentOrganizationReviewSpec> getAll(List<String> keys) {

		Map<Long, AgentOrganizationReviewSpec> result = new FluentHashMap<>();

		List<Long> longKeys = new ArrayList<>(keys.size());

		// Convert to Long keys
		keys.forEach(k -> {
			longKeys.add(Long.parseLong(k));
		});

		ofy().load().type(AgentOrganizationReviewTable.class).ids(longKeys).forEach((k, v) -> {
			
			AgentOrganizationReviewSpec spec = EntityHelper.toObjectModel(v);
			
			result.put(k, spec);
		});

		return result;
	}

	@Override
	public SearchableUISpec searchableUiSpec() {
		return null;
	}

}
