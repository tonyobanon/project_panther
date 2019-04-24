package com.re.paas.internal.models.tables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.ActivityStreamTableSpec;

public class ActivityStreamTable implements BaseTable {

	Long id;
	String subject;
	String person;
	String subjectImage;
	String personImage;
	String activity;
	Integer likes;
	Date date;

	@Override
	public String hashKey() {
		return ActivityStreamTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition subjectIndex = new IndexDefinition(ActivityStreamTableSpec.SUBJECT_INDEX, Type.GSI)
				.addHashKey(ActivityStreamTableSpec.SUBJECT);

		IndexDefinition personIndex = new IndexDefinition(ActivityStreamTableSpec.PERSON_INDEX, Type.GSI)
				.addHashKey(ActivityStreamTableSpec.PERSON);

		IndexDefinition dateIndex = new IndexDefinition(ActivityStreamTableSpec.DATE_INDEX, Type.GSI)
				.addHashKey(ActivityStreamTableSpec.DATE);

		indexes.add(subjectIndex);
		indexes.add(personIndex);
		indexes.add(dateIndex);

		return indexes;
	}

}
