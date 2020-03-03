package com.re.paas.integrated.tables.defs.users;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.users.UserFormValueTableSpec;

public class UserFormValueTable implements BaseTable {

	Long id;
	String fieldId;
	Integer userId;
	String value;
	Integer dateCreated;
	Integer dateUpdated;

	@Override
	public String hashKey() {
		return UserFormValueTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition fieldIndex = new IndexDefinition(UserFormValueTableSpec.FIELD_INDEX, Type.GSI)
				.addHashKey(UserFormValueTableSpec.FIELD_ID);
		
		IndexDefinition userIndex = new IndexDefinition(UserFormValueTableSpec.USER_INDEX, Type.GSI)
				.addHashKey(UserFormValueTableSpec.USER_ID).addRangehKey(UserFormValueTableSpec.FIELD_ID);

		indexes.add(fieldIndex);
		indexes.add(userIndex);

		return indexes;
	}

}
