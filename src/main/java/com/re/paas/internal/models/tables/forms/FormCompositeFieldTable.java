package com.re.paas.internal.models.tables.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.FormCompositeFieldTableSpec;

public class FormCompositeFieldTable implements BaseTable {
	
	String id;
	ClientRBRef title;
	String section;
	String context;
	
	Integer sortOrder;
	Boolean isVisible;
	Boolean isRequired;
	Boolean isDefault;
	
	Date dateCreated;
	
	Boolean allowMultipleChoice;
	
	Map<ClientRBRef, Object> options;
	
	String itemsSource;
	List<String> defaultSelections;
	
	@Override
	public String hashKey() {
		return FormCompositeFieldTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition sectionIndex = new IndexDefinition(FormCompositeFieldTableSpec.SECTION_INDEX, Type.GSI)
		.addHashKey(FormCompositeFieldTableSpec.SECTION);

		indexes.add(sectionIndex);
		
		return indexes;
	}
	
}
