package com.re.paas.internal.tables.defs.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.tables.spec.base.CronJobTableSpec;

public class CronJobTable implements BaseTable {

	Long id;

	String name;

	Integer interval;

	Integer cronType;

	String taskModelName;

	String job; // Binary data encoded as base64

	Boolean isReady;

	Integer totalExecutionCount;

	Integer maxExecutionCount;

	boolean isInternal;

	Date dateCreated;

	@Override
	public String hashKey() {
		return CronJobTableSpec.ID;
	}

	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();

		IndexDefinition intervalIndex = new IndexDefinition(CronJobTableSpec.INTERVAL_INDEX, Type.GSI)
				.addHashKey(CronJobTableSpec.INTERVAL);

		IndexDefinition isInternalIndex = new IndexDefinition(CronJobTableSpec.IS_INTERNAL_INDEX, Type.GSI)
				.addHashKey(CronJobTableSpec.IS_INTERNAL);

		indexes.add(intervalIndex);
		indexes.add(isInternalIndex);

		return indexes;
	}

}
