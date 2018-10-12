package com.re.paas.apps.rex.models.listables;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.entites.directory.AgentOrganizationMessageEntity;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.apps.rex.classes.spec.BaseAgentOrganizationMessageSpec;
import com.re.paas.apps.rex.classes.spec.IssueResolution;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.models.listables.IndexedNameTypes;

public class AgentOrganizationMessageList extends AbstractAgentOrganizationList<BaseAgentOrganizationMessageSpec> {

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.AGENT_ORGANIZATION_MESSAGE;
	}

	@Override
	public boolean authenticate(ListingType type, Long principal, List<ListingFilter> listingFilters) {

		String role = BaseUserModel.getRole(principal);
		boolean canAccess = RoleModel.isAccessAllowed(role, AgentOrganizationFunctionalities.LIST_AGENT_ORGANIZATION_MESSAGES);

		if (!canAccess) {
			return false;
		}

		return canAccess && canProvisionOrganization(principal, listingFilters);
	}

	@Override
	public Class<AgentOrganizationMessageEntity> entityType() {
		return AgentOrganizationMessageEntity.class;
	}

	@Override
	public boolean searchable() {
		return false;
	}

	@Override
	public Map<Long, BaseAgentOrganizationMessageSpec> getAll(List<String> keys) {

		Map<Long, BaseAgentOrganizationMessageSpec> result = new FluentHashMap<>();

		List<Long> longKeys = new ArrayList<>(keys.size());

		// Convert to Long keys
		keys.forEach(k -> {
			longKeys.add(Long.parseLong(k));
		});

		ofy().load().type(AgentOrganizationMessageEntity.class).ids(longKeys).forEach((k, v) -> {
			
			BaseAgentOrganizationMessageSpec spec = new BaseAgentOrganizationMessageSpec()
					.setId(v.getId())
					.setName(v.getName())
					.setMobile(v.getMobile())
					.setEmail(v.getEmail())
					.setIsRead(v.getIsRead())
					.setResolution(IssueResolution.from(v.getResolution()))
					.setTruncatedMessage(ResponseUtil.truncate(v.getMessage(), 10))
					.setDateCreated(v.getDateCreated())
					.setDateUpdated(v.getDateUpdated());
			
			
			result.put(k, spec);
		});

		return result;
	}

	@Override
	public SearchableUISpec searchableUiSpec() {
		return null;
	}

}
