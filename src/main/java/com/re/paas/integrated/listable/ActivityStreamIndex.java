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
import com.re.paas.integrated.tables.defs.base.ActivityStreamTable;
import com.re.paas.integrated.tables.spec.base.ActivityStreamTableSpec;
import com.re.paas.internal.classes.spec.ActivitySpec;

public class ActivityStreamIndex implements ListableIndex<ActivitySpec> {

	@Override
	public Map<String, ActivitySpec> getAll(List<String> keys) {	
	
		Map<String, ActivitySpec> result = new FluentHashMap<>();

		GetItemsSpec spec = GetItemsSpec.forKeys(
				keys.stream().map(id -> new PrimaryKey(ActivityStreamTableSpec.ID, id)).collect(Collectors.toList()),
				ActivityStreamTableSpec.SUBJECT_IMAGE, ActivityStreamTableSpec.PERSON_IMAGE, ActivityStreamTableSpec.ACTIVITY,
				ActivityStreamTableSpec.LIKES, ActivityStreamTableSpec.DATE);

		Database.get().batchGetItem(new BatchGetItemRequest().addRequestItem(ActivityStreamTable.class, spec))
				.getResponses(ActivityStreamTable.class).forEach(i -> {

					ActivitySpec Spec = new ActivitySpec()
							.setId(i.getLong(ActivityStreamTableSpec.ID))
							.setSubjectImage(i.getString(ActivityStreamTableSpec.SUBJECT_IMAGE))
							.setPersonImage(i.getString(ActivityStreamTableSpec.PERSON_IMAGE))
							.setHtml(i.getString(ActivityStreamTableSpec.ACTIVITY))
							.setLikes(i.getInt(ActivityStreamTableSpec.LIKES))
							.setDate(i.getDate(ActivityStreamTableSpec.DATE));
							
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
		return "activity_stream";
	}
}
