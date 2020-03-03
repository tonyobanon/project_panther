package com.re.paas.integrated.listable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.listable.ListableIndex;
import com.re.paas.integrated.models.BaseUserModel;
import com.re.paas.integrated.tables.defs.users.BaseUserTable;
import com.re.paas.integrated.tables.spec.users.BaseUserTableSpec;
import com.re.paas.internal.classes.spec.BaseUserSpec;

public class BaseUsersIndex implements ListableIndex<BaseUserSpec> {

	@Override
	public Map<String, BaseUserSpec> getAll(List<String> keys) {

		Map<String, BaseUserSpec> result = new FluentHashMap<>();

		GetItemsSpec spec = GetItemsSpec.forKeys(
				keys.stream().map(id -> new PrimaryKey(BaseUserTableSpec.ID, id)).collect(Collectors.toList()),
				BaseUserTableSpec.FIRST_NAME, BaseUserTableSpec.MIDDLE_NAME, BaseUserTableSpec.LAST_NAME,
				BaseUserTableSpec.ROLE, BaseUserTableSpec.DATE_CREATED, BaseUserTableSpec.DATE_UPDATED);

		Database.get().batchGetItem(new BatchGetItemRequest().addRequestItem(BaseUserTable.class, spec))
				.getResponses(BaseUserTable.class).forEach(i -> {

					BaseUserSpec Spec = new BaseUserSpec().setId(i.getLong(BaseUserTableSpec.ID))
							.setRole(i.getString(BaseUserTableSpec.ROLE)).setName(BaseUserModel.getPersonName(i, false))
							.setDateCreated(i.getDate(BaseUserTableSpec.DATE_CREATED))
							.setDateUpdated(i.getDate(BaseUserTableSpec.DATE_UPDATED));

					result.put(Spec.getId().toString(), Spec);

				});

		return result;
	}

	@Override
	public String id() {
		return "base";
	}

	@Override
	public String namespace() {
		return "users";
	}

	@Override
	public ClientSearchSpec clientSearchSpec() {
		return new ClientSearchSpec().setName(ClientRBRef.get("users")).setDisplayInAppShell(true)
				.setListingPageUrl("/users-search");
	}
}
