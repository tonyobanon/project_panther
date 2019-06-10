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
import com.re.paas.apps.rex.classes.spec.BaseAgentOrganizationSpec;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.models.tables.AgentOrganizationTable;
import com.re.paas.internal.locations.LocationModel;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;

public class AgentOrganizationList extends Listable<BaseAgentOrganizationSpec> {

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.AGENT_ORGANIZATION;
	}

	@Override
	public boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters) {
		String role = BaseUserModel.getRole(userId);
		switch (type) {
		case SEARCH:
			return RoleModel.isAccessAllowed(role, AgentOrganizationFunctionalities.SEARCH_AGENT_ORGANIZATION);
		case LIST:
		default:
			return RoleModel.isAccessAllowed(role, AgentOrganizationFunctionalities.LIST_AGENT_ORGANIZATION);
		}
	}

	@Override
	public Class<AgentOrganizationTable> entityType() {
		return AgentOrganizationTable.class;
	}

	@Override
	public boolean searchable() {
		return true;
	}

	@Override
	public Map<Long, BaseAgentOrganizationSpec> getAll(List<String> keys) {

		Map<Long, BaseAgentOrganizationSpec> result = new FluentHashMap<>();

		List<Long> longKeys = new ArrayList<>(keys.size());

		// Convert to Long keys
		keys.forEach(k -> {
			longKeys.add(Long.parseLong(k));
		});

		ofy().load().type(AgentOrganizationTable.class).ids(longKeys).forEach((k, v) -> {

			BaseAgentOrganizationSpec spec = new BaseAgentOrganizationSpec().setId(v.getId()).setName(v.getName())
					.setEmail(v.getEmail()).setLogo(v.getLogo()).setRating(v.getRating()).setAddress(v.getAddress())
					.setCity(v.getCity()).setCityName(LocationModel.getCityName(v.getCity().toString()))
					.setTerritory(v.getTerritory()).setTerritoryName(LocationModel.getTerritoryName(v.getTerritory()))
					.setCountry(v.getCountry()).setCountryName(LocationModel.getCountryName(v.getCountry()));

			result.put(k, spec);
		});

		return result;
	}

	@Override
	public SearchableUISpec searchableUiSpec() {
		return new SearchableUISpec().setName("agent_organization").setListingPageUrl("/agent-organization-search");
	}

}
