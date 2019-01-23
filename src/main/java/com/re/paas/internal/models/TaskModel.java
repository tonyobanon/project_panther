package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.ClientResources.ClientRBRefEntry;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.classes.TaskExecutionOutcome;
import com.re.paas.api.classes.TimeUnit;
import com.re.paas.api.errors.impl.CronError;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.tasks.CronJob;
import com.re.paas.api.tasks.TaskImage;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.classes.CronInterval;
import com.re.paas.internal.classes.CronJobGreediness;
import com.re.paas.internal.classes.CronType;
import com.re.paas.internal.compute.Scheduler;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.entites.CronJobEntity;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.tasks.ModelTask;
import com.re.paas.internal.utils.BackendObjectMarshaller;

public class TaskModel implements BaseModel {

	@Override
	public String path() {
		return "core/task_model";
	}

	@Override
	public void preInstall() {

		for (CronInterval interval : CronInterval.values()) {
			ConfigModel.put(ConfigKeys.$INTERVAL_CRON_LAST_EXECUTION_TIME.replace("$INTERVAL", interval.name()),
					BackendObjectMarshaller.marshal(Dates.now()));
		}
	}

	@Override
	public void start() {
		for (CronInterval interval : CronInterval.values()) {
			Scheduler.getDefaultExecutor().scheduleWithFixedDelay(() -> {
				executeAll(interval);
			}, 1, interval.getMinutesOffset() * 60, java.util.concurrent.TimeUnit.SECONDS);
		}
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_CRON_TASK_MODEL)
	public static Map<String, ClientRBRef> getImageNames() {
		return TaskImage.getDelegate().getTaskModelNames();
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_CRON_TASK_MODEL)
	public static List<AbstractField> getImageFields(String name) {
		return TaskImage.getDelegate().getTaskModel(name).fields();
	}

	protected static Date getLastExecutionTime(CronInterval interval) {
		try {
			return BackendObjectMarshaller.unmarshalDate(ConfigModel
					.get(ConfigKeys.$INTERVAL_CRON_LAST_EXECUTION_TIME.replace("$INTERVAL", interval.name())));
		} catch (ParseException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	protected static Long getLastExecutionTimeOffset(TimeUnit unit, CronInterval interval) {
		Long offset = Utils.getTimeOffset(unit, getLastExecutionTime(interval));
		assert offset < 0;
		return offset;
	}

	protected static LocalDate getNextExecutionTime(CronInterval interval) {

		LocalDate lastExecTime = getLastExecutionTime(interval).toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();

		LocalDate nextExecTime = lastExecTime.plus(interval.getMinutesOffset(), ChronoUnit.MINUTES);

		if (interval.getGreediness().equals(CronJobGreediness.RELUNCTANT)) {
			nextExecTime = lastExecTime.plus(interval.getMinutesOffset(), ChronoUnit.MINUTES);
		}

		return nextExecTime;
	}

	protected static Long getNextExecutionTimeOffset(TimeUnit unit, CronInterval interval) {

		Long offset = Utils.getTimeOffset(unit, LocalDate.now(), getNextExecutionTime(interval));
		assert offset > 0;

		return offset;
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_CRON_TASK)
	public static Long newTask(String name, CronInterval interval, ModelTask task, Integer maxExecutionCount,
			boolean isInternal) {

		if (maxExecutionCount <= 0) {
			return null;
		}

		CronJobEntity e = new CronJobEntity().setName(name).setInterval(interval.getValue())
				.setCronType(CronType.MODEL_TASK.getValue()).setTask(task).setIsReady(false).setTotalExecutionCount(0)
				.setMaxExecutionCount(maxExecutionCount).setIsInternal(isInternal).setDateCreated(Dates.now());

		return ofy().save().entity(e).now().getId();
	}

	protected static Long newTask(String name, CronInterval interval, CronJob job, Integer maxExecutionCount,
			boolean isInternal) {

		if (maxExecutionCount <= 0) {
			return null;
		}

		CronJobEntity e = new CronJobEntity().setName(name).setInterval(interval.getValue())
				.setCronType(CronType.STANDALONE_JOB.getValue()).setJob(job).setIsReady(false).setTotalExecutionCount(0)
				.setMaxExecutionCount(maxExecutionCount).setIsInternal(isInternal).setDateCreated(Dates.now());

		return ofy().save().entity(e).now().getId();
	}

	@ModelMethod(functionality = RoleFunctionalities.DELETE_CRON_TASK)
	public static void deleteTask(Long id) {

		CronJobEntity e = ofy().load().type(CronJobEntity.class).id(id).safe();

		if (e.getIsInternal()) {
			throw new PlatformException(CronError.CANNOT_DELETE_INTERNAL_CRON_JOB);
		}

		ofy().delete().type(CronJobEntity.class).id(id).now();
	}

	@PlatformInternal
	public static Map<Long, TaskExecutionOutcome> executeAll(CronInterval interval) {

		Map<Long, TaskExecutionOutcome> result = new HashMap<Long, TaskExecutionOutcome>();

		List<CronJobEntity> entitiesToSave = new ArrayList<>();
		List<CronJobEntity> entitiesToDelete = new ArrayList<>();

		EntityUtils.lazyQuery(CronJobEntity.class, QueryFilter.get("interval", interval.getValue())).forEach(e -> {

			if (e.getMaxExecutionCount() > 0 && e.getTotalExecutionCount() >= e.getMaxExecutionCount()) {
				entitiesToDelete.add(e);
				return;
			}

			if (!e.getIsReady()) {

				e.setIsReady(true);

				if (interval.getGreediness().equals(CronJobGreediness.RELUNCTANT)) {
					entitiesToSave.add(e);
					return;
				}
			}

			TaskExecutionOutcome outcome = null;

			try {

				// Based on cron type, execute
				CronType type = CronType.from(e.getCronType());

				switch (type) {

				case MODEL_TASK:
					ModelTask task = e.getTask();
					TaskImage model = TaskImage.getDelegate().getTaskModel(task.getModelName());
					outcome = model.setParameters(task.getParameters()).call();
					break;

				case STANDALONE_JOB:
					outcome = e.getJob().call();
					break;
				}

				// Add to event stream

			} catch (Exception ex) {

				outcome = TaskExecutionOutcome.FAILURE.setMessage(ClientRBRef.forEach("Error_occurred_while_executing_task")
						.add(new ClientRBRefEntry(e.getId() + ": " + Exceptions.recurseCause(ex).getMessage(),
								ClientResources.ClientRBRefEntryType.NON_TRANSLATE)));

				// Add error scenario to event streams
			}

			entitiesToSave.add(e.setTotalExecutionCount(e.getTotalExecutionCount() + 1));

			result.put(e.getId(), outcome);

		});

		if (!entitiesToDelete.isEmpty()) {
			ofy().delete().entities(entitiesToDelete).now();
		}

		if (!entitiesToSave.isEmpty()) {
			ofy().save().entities(entitiesToSave).now();
		}

		ConfigModel.put(ConfigKeys.$INTERVAL_CRON_LAST_EXECUTION_TIME.replace("$INTERVAL", interval.name()),
				BackendObjectMarshaller.marshal(Dates.now()));

		return result;
	}
}
