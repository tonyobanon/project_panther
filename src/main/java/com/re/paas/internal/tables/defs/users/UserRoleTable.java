package com.re.paas.internal.tables.defs.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.tables.spec.users.UserRoleTableSpec;

public class UserRoleTable implements BaseTable {

	String name;
	List<String> spec;
	Boolean isDefault;
	String realm;
	Date dateCreated;
	
	@Override
	public String hashKey() {
		return UserRoleTableSpec.NAME;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition isDefaultIndex = new IndexDefinition(UserRoleTableSpec.IS_DEFAULT_INDEX, Type.GSI)
				.addHashKey(UserRoleTableSpec.IS_DEFAULT);

		IndexDefinition realmIndex = new IndexDefinition(UserRoleTableSpec.REALM_INDEX, Type.GSI)
				.addHashKey(UserRoleTableSpec.REALM);

		indexes.add(isDefaultIndex);
		indexes.add(realmIndex);

		return indexes;
	}
}
