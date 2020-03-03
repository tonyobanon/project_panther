package com.re.paas.integrated.tables.defs.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.users.ApplicationFormValueTableSpec;

public class ApplicationFormValueTable implements BaseTable {

	Long id;
	String fieldId;
	String applicationId;
	String value;
	Date dateUpdated;

	@Override
	public String hashKey() {
		return ApplicationFormValueTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition fieldIndex = new IndexDefinition(ApplicationFormValueTableSpec.FIELD_INDEX, Type.GSI)
				.addHashKey(ApplicationFormValueTableSpec.FIELD_ID);

		IndexDefinition applicationIndex = new IndexDefinition(ApplicationFormValueTableSpec.APPLICATION_INDEX,
				Type.GSI)
				.addHashKey(ApplicationFormValueTableSpec.APPLICATION_ID)
				.addRangehKey(ApplicationFormValueTableSpec.FIELD_ID);

		indexes.add(fieldIndex);
		indexes.add(applicationIndex);

		return indexes;
	}
}
