package com.re.paas.integrated.listable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.listable.ListableIndex;
import com.re.paas.api.realms.Realm;
import com.re.paas.integrated.models.ApplicationModel;
import com.re.paas.integrated.models.RoleModel;
import com.re.paas.integrated.tables.defs.users.ApplicationTable;
import com.re.paas.integrated.tables.spec.users.ApplicationTableSpec;
import com.re.paas.internal.classes.ApplicationStatus;
import com.re.paas.internal.classes.spec.BaseApplicationSpec;

public abstract class AbstractApplicationsIndex implements ListableIndex<BaseApplicationSpec> {

	@Override
	public Map<String, BaseApplicationSpec> getAll(List<String> keys) {

		Map<String, BaseApplicationSpec> result = new FluentHashMap<>();

		// Fetch roles
		Map<Long, String> roles = ApplicationModel.getApplicationRoles(keys.stream().map(k -> Long.parseLong(k)).collect(Collectors.toList()));

		// Fetch realms
		Map<String, Realm> realms = RoleModel.getRealms(roles.values());

		GetItemsSpec spec = GetItemsSpec.forKeys(
				keys.stream().map(id -> new PrimaryKey(ApplicationTableSpec.ID, id)).collect(Collectors.toList()),
				ApplicationTableSpec.STATUS, ApplicationTableSpec.DATE_CREATED,
				ApplicationTableSpec.DATE_UPDATED);


		Database.get().batchGetItem(new BatchGetItemRequest().addRequestItem(ApplicationTable.class, spec))
				.getResponses(ApplicationTable.class).forEach(i -> {
					
					Long applicationId = i.getLong(ApplicationTableSpec.ID);
					String role = roles.get(applicationId);
					Realm realm = realms.get(role);
					
					String title = ApplicationModel.getPersonName(applicationId, realm);
					
					BaseApplicationSpec Spec = new BaseApplicationSpec()
							.setId(applicationId)
							.setRole(role)
							.setStatus(ApplicationStatus.from(i.getInt(ApplicationTableSpec.STATUS)))
							.setTitle(title)
							.setDateCreated(i.getDate(ApplicationTableSpec.DATE_CREATED))
							.setDateUpdated(i.getDate(ApplicationTableSpec.DATE_UPDATED));

					result.put(Spec.getId().toString(), Spec);
					
				});

		return result;
	}

}
