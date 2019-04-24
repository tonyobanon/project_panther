package com.re.paas.internal.models.tables.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.FormSimpleFieldTableSpec;

public class FormSimpleFieldTable implements BaseTable {

	String id;
	ClientRBRef title;
	String section;
	String inputType;
	String context;
	
	Integer sortOrder;
	String defaultValue;
	Boolean isVisible;
	Boolean isRequired;
	Boolean isDefault;
	
	Date dateCreated;

	@Override
	public String hashKey() {
		return FormSimpleFieldTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition sectionIndex = new IndexDefinition(FormSimpleFieldTableSpec.SECTION_INDEX, Type.GSI)
		.addHashKey(FormSimpleFieldTableSpec.SECTION);

		indexes.add(sectionIndex);
		
		return indexes;
	}

}
