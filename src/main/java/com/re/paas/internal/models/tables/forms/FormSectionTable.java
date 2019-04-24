package com.re.paas.internal.models.tables.forms;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.FormSectionTableSpec;

public class FormSectionTable implements BaseTable {

	String id;
	ClientRBRef title;
	Integer type;
	ClientRBRef description;
	String realm;
	
	@Override
	public String hashKey() {
		return FormSectionTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition titleIndex = new IndexDefinition(FormSectionTableSpec.TITLE_INDEX, Type.GSI)
				.setQueryOptimzed(true)
				.addHashKey(FormSectionTableSpec.TITLE);

		IndexDefinition typeIndex = new IndexDefinition(FormSectionTableSpec.TYPE_INDEX, Type.GSI)
		.addHashKey(FormSectionTableSpec.TYPE);
		
		IndexDefinition realmIndex = new IndexDefinition(FormSectionTableSpec.REALM_INDEX, Type.GSI)
				.addHashKey(FormSectionTableSpec.REALM);
		
		indexes.add(titleIndex);
		indexes.add(typeIndex);
		indexes.add(realmIndex);
		
		return indexes;
	}
}
