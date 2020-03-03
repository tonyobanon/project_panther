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
import com.re.paas.integrated.tables.defs.base.CronJobTable;
import com.re.paas.integrated.tables.spec.base.CronJobTableSpec;
import com.re.paas.internal.classes.CronType;
import com.re.paas.internal.classes.TaskInterval;
import com.re.paas.internal.classes.spec.BaseCronJobSpec;

public class CronJobIndex implements ListableIndex<BaseCronJobSpec> {

	@Override
	public String id() {
		return "base";
	}

	@Override
	public String namespace() {
		return "cron";
	}

	@Override
	public Map<String, BaseCronJobSpec> getAll(List<String> keys) {

		Map<String, BaseCronJobSpec> result = new FluentHashMap<>();

		GetItemsSpec spec = GetItemsSpec.forKeys(
				keys.stream().map(id -> new PrimaryKey(CronJobTableSpec.ID, id)).collect(Collectors.toList()),
				CronJobTableSpec.NAME, CronJobTableSpec.INTERVAL, CronJobTableSpec.CRON_TYPE, CronJobTableSpec.IS_READY,
				CronJobTableSpec.TOTAL_EXECUTION_COUNT, CronJobTableSpec.MAX_EXECUTION_COUNT,
				CronJobTableSpec.IS_INTERNAL, CronJobTableSpec.DATE_CREATED);

		Database.get().batchGetItem(new BatchGetItemRequest().addRequestItem(CronJobTable.class, spec))
				.getResponses(CronJobTable.class).forEach(i -> {

					BaseCronJobSpec Spec = new BaseCronJobSpec().setId(i.getLong(CronJobTableSpec.ID))
							.setName(i.getString(CronJobTableSpec.NAME))
							.setInterval(TaskInterval.from(i.getInt(CronJobTableSpec.INTERVAL)))
							.setCronType(CronType.from(i.getInt(CronJobTableSpec.CRON_TYPE)))
							.setIsReady(i.getBoolean(CronJobTableSpec.IS_READY))
							.setTotalExecutionCount(i.getInt(CronJobTableSpec.TOTAL_EXECUTION_COUNT))
							.setMaxExecutionCount(i.getInt(CronJobTableSpec.MAX_EXECUTION_COUNT))
							.setIsInternal(i.getBoolean(CronJobTableSpec.IS_INTERNAL))
							.setDateCreated(i.getDate(CronJobTableSpec.DATE_CREATED));

					result.put(Spec.getId().toString(), Spec);
				});

		return result;
	}
}
