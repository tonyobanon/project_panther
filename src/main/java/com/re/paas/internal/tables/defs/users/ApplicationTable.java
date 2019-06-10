package com.re.paas.internal.tables.defs.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.tables.spec.users.ApplicationTableSpec;

public class ApplicationTable implements BaseTable {

	Long id;
	String role;
	Integer status;
	String ref;
	Date dateCreated;
	Date dateUpdated;

	@Override
	public String hashKey() {
		return ApplicationTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition roleIndex = new IndexDefinition(ApplicationTableSpec.ROLE_INDEX, Type.GSI)
				.addHashKey(ApplicationTableSpec.ROLE);

		IndexDefinition statusIndex = new IndexDefinition(ApplicationTableSpec.STATUS_INDEX, Type.GSI)
				.addHashKey(ApplicationTableSpec.STATUS)
				.setQueryOptimzed(true);

		IndexDefinition refIndex = new IndexDefinition(ApplicationTableSpec.REF_INDEX, Type.GSI)
				.addHashKey(ApplicationTableSpec.REF);

		indexes.add(roleIndex);
		indexes.add(statusIndex);
		indexes.add(refIndex);

		return indexes;
	}

}