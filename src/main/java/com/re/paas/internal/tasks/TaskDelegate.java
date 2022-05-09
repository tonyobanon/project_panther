package com.re.paas.internal.tasks;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.infinispan.manager.DefaultCacheManager;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.AsyncDistributedMap;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ExternalContext;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.runtime.spi.ResourcesInitResult;
import com.re.paas.api.runtime.spi.ShutdownPhase;
import com.re.paas.api.tasks.AbstractTaskDelegate;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.api.tasks.Task;
import com.re.paas.api.tasks.TaskModel;
import com.re.paas.api.utils.Dates;
import com.re.paas.internal.classes.ClassUtil;
import com.re.paas.internal.clustering.ClusterWideTask;

public class TaskDelegate extends AbstractTaskDelegate {

	private static Logger LOG = LoggerFactory.get().getLog(TaskDelegate.class);

	private static final String jobRunnerTaskName = "cache_evicter";
	private static ScheduledExecutorService jobRunnerExecutor;

	private static final String nextExecutorInvokation = "nextExecutorInvokation";
	private static final String upperExecutorInvokation = "upperExecutorInvokation";

	private static final Integer defaultSchedulerLeewayInSecs = 5;

	private static final long INTERVAL_IN_SECS = 75;

	private static final String TASK_DEFINITION_STORE_NAME = "_td";

	private static final String TASK_MODEL_NAMESPACE = "_tm";

	@Override
	public DelegateInitResult init() {

		ClusteringServices cs = ClusteringServices.get();

		if (cs.isExecutioner()) {

			ClusterWideTask task = new ClusterWideTask(jobRunnerTaskName, () -> {

				TaskDelegate delegate = (TaskDelegate) TaskModel.getDelegate();

				if (delegate.intervalInSecs() < defaultSchedulerLeewayInSecs * 2) {

					Exceptions.throwRuntime("Task executor must have an interval that is apart by at least "
							+ defaultSchedulerLeewayInSecs * 2 + " seconds");
				}

				delegate.createResourceMaps();

				// Register task models
				ResourcesInitResult r = delegate.addResources(delegate::add0);

				LOG.debug("Discovered " + r.getCount() + " tasks");

				r.getErrors().forEach(err -> {
					LOG.error(err.getCulprit() + ": " + err.getErrorMessage());
				});

				jobRunnerExecutor = Executors.newSingleThreadScheduledExecutor();

				jobRunnerExecutor.scheduleAtFixedRate(delegate::execute, 0L, delegate.intervalInSecs(),
						TimeUnit.SECONDS);

			});

			cs.addClusterWideTask(task);
		}

		return DelegateInitResult.SUCCESS;
	}

	@Override
	public void shutdown(ShutdownPhase phase) {

		ClusteringServices cs = ClusteringServices.get();

		if (cs.isExecutioner()) {
			assert jobRunnerExecutor != null;

			jobRunnerExecutor.shutdown();
		}

	}

	@Override
	public long intervalInSecs() {
		return INTERVAL_IN_SECS;
	}

	private static DefaultCacheManager getCacheManager() {
		return (DefaultCacheManager) ClusteringServices.get().getCacheManager();
	}

	private void setNextTaskExecutorInvokation(Instant i) {
		getCacheManager().getCache().put(nextExecutorInvokation, i);
	}

	private Instant getNextTaskExecutorInvokation() {
		return (Instant) getCacheManager().getCache().get(nextExecutorInvokation);
	}

	private void setUpperTaskExecutorInvokation(Instant i) {
		getCacheManager().getCache().put(upperExecutorInvokation, i);
	}

	private Instant getUpperTaskExecutorInvokation() {
		return (Instant) getCacheManager().getCache().get(upperExecutorInvokation);
	}

	@Override
	public boolean requiresDistributedStore() {
		return true;
	}

	@Override
	public List<Object> distributedStoreNames() {
		return Arrays.asList(TASK_DEFINITION_STORE_NAME);
	}

	private static final CronParser getCronParser() {
		CronParser parser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));
		return parser;
	}

	@BlockerTodo("We are currently not using TaskExecutionOutcome, since we really cannot wait")
	private void execute() {

		final Instant now = Dates.getInstant();

		setNextTaskExecutorInvokation(now.plusSeconds(intervalInSecs()));
		setUpperTaskExecutorInvokation(now.plusSeconds(intervalInSecs() * 2));

		ExecutorFactory execFactory = ExecutorFactory.get();

		getTaskDefinitions().values().join().forEach(definition -> {

			Task task = definition.getTask();

			setNextExecutionTime(definition);

			Boolean execute = canExecuteRightNow(now, definition);

			if (execute) {

				ParameterizedExecutable<Task, Void> executable = execFactory
						.buildFunction(new ObjectWrapper<ClassLoader>(task.getClass().getClassLoader()), (p) -> {
							p.call();
							return null;
						}, task, new ExternalContext(ClassLoaders.getId(task.getClass()), false, Affinity.ANY));

				// Execute task
				ClusteringServices.get().execute(executable, false);

				// Set Last execution time
				definition.setLastExecutionTime(Dates.getInstant());
			}
		});
	}

	private boolean canExecuteRightNow(Instant now, TaskDefinition definition) {

		long secsLeeway = defaultSchedulerLeewayInSecs;

		if (definition.getNextExecutionTime() == null) {
			return false;
		}

		Instant nextExecution = definition.getNextExecutionTime();

		return nextExecution.isAfter(now.minusSeconds(secsLeeway))
				&& nextExecution.isBefore(now.plusSeconds(secsLeeway));
	}

	private void setNextExecutionTime(TaskDefinition definition) {

		Instant instant = Dates.getInstant();

		Task task = definition.getTask();

		// Return null if the current time falls out of the start and end date

		if (task.startDate() != null && instant.isBefore(task.startDate().toInstant())) {
			return;
		}

		if (task.stopDate() != null && instant.isAfter(task.stopDate().toInstant())) {
			return;
		}

		// Return null if the next execution time is closer to the
		// <upperTaskExecutorInvokation> than
		// it is to <nextTaskExecutorInvokation>

		Cron cron = getCronParser().parse(task.interval());

		Instant fromDate = definition.getLastExecutionTime() != null ? definition.getLastExecutionTime()
				: definition.getDateAdded();

		ZonedDateTime from = ZonedDateTime.ofInstant(fromDate, ZoneId.systemDefault());

		ZonedDateTime to = ExecutionTime.forCron(cron).nextExecution(from).get();

		Instant nextTaskExecutorInvokation = getNextTaskExecutorInvokation();
		Instant upperTaskExecutorInvokation = getUpperTaskExecutorInvokation();

		Instant closest = Dates.getNearestDate(to.toInstant(), nextTaskExecutorInvokation, upperTaskExecutorInvokation);

		if (closest == nextTaskExecutorInvokation) {
			definition.setNextExecutionTime(nextTaskExecutorInvokation);
		}
	}

	@Override
	protected ResourceStatus add(Class<TaskModel> clazz) {
		return add0(clazz);
	}

	private ResourceStatus add0(Class<TaskModel> clazz) {

		TaskModel o = ClassUtil.createInstance(clazz);

		if (getTaskModelResourceMap().containsKey(o.name())) {
			return ResourceStatus.ERROR.setMessage("Task model '" + o.name() + "' already exists");
		}

		// Add Task model
		getTaskModelResourceMap().put(o.name(), o);

		// If task model has a default associated task, add now
		if (o.fields() == null) {

			assert o.defaultParameters() != null;

			Task task = o.build(o.defaultParameters());

			registerTask(new TaskDefinition(task, false, o.name()));
		}

		return ResourceStatus.UPDATED;
	}

	private static int getSuccessiveIntervalInSeconds(String expression) {

		CronParser parser = getCronParser();

		ZonedDateTime now = ZonedDateTime.now();
		ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(expression));

		ZonedDateTime nextExecution = executionTime.nextExecution(now).get();
		ZonedDateTime upperExecution = executionTime.nextExecution(nextExecution).get();

		return upperExecution.get(ChronoField.INSTANT_SECONDS) - nextExecution.get(ChronoField.INSTANT_SECONDS);
	}

	@BlockerTodo("See comments")
	@Override
	protected ResourceStatus remove(Class<TaskModel> clazz) {

		TaskModel o = ClassUtil.createInstance(clazz);

		// Todo: Work with executor service to prevent deadlock

		// Remove task definitions created from this model

		for (TaskDefinition t : getTaskDefinitions().values().join()) {
			if (t.getModelName().equals(o.name())) {

				getTaskDefinitions().remove(t.getTask().id());
				break;
			}
		}

		// Remove Task models

		getTaskModelResourceMap().remove(o.name());

		return ResourceStatus.UPDATED;
	}

	private void registerTask(TaskDefinition def) {

		Task task = def.getTask();

		if (getSuccessiveIntervalInSeconds(task.interval()) < intervalInSecs() * 2) {
			Exceptions.throwRuntime("Task: " + task.id() + " must have an interval that is apart by at least "
					+ intervalInSecs() * 2 + " seconds");
		}

		AsyncDistributedMap<String, TaskDefinition> m = getTaskDefinitions();

		if (m.containsKey(task.id()).join()) {
			Exceptions.throwRuntime("Task '" + task.id() + "' already exists");
		}

		m.put(task.id(), def.setDateAdded(Dates.getInstant()));
	}

	@Override
	public void registerTask(String modelName, Map<String, Object> parameters) {

		TaskModel model = getTaskModelResourceMap().get(modelName);

		Task task = model.build(parameters);

		registerTask(new TaskDefinition(task, true, modelName));
	}

	@Override
	public void registerTask(Task task) {
		registerTask(new TaskDefinition(task, true, null));
	}

	@Override
	public void removeTask(String id) {

		TaskDefinition def = getTaskDefinitions().remove(id).join();

		if (def == null) {
			Exceptions.throwRuntime("Task '" + id + "' cannot be deleted - Not found");
		}

		if (!def.getIsDeletable()) {
			getTaskDefinitions().put(id, def).join();
			Exceptions.throwRuntime("Task '" + id + "' cannot be deleted - Not allowed");
		}
	}

	@Override
	public Map<String, String> getTaskModelNames() {

		Map<String, String> result = new HashMap<>();

		getTaskModelResourceMap().values().forEach((v) -> {

			TaskModel m = (TaskModel) v;

			// Note: there are some task models from which user-generated tasks
			// cannot be created, here we need to check

			if (m.fields() != null) {
				result.put(m.name(), m.title());
			}
		});
		return result;
	}

	@Override
	public TaskModel getTaskModel(String name) {
		return (TaskModel) getTaskModelResourceMap().get(name);
	}

	private final AsyncDistributedMap<String, TaskDefinition> getTaskDefinitions() {
		@SuppressWarnings("unchecked")
		AsyncDistributedMap<String, TaskDefinition> r = (AsyncDistributedMap<String, TaskDefinition>) super.getDistributedStore(
				TASK_DEFINITION_STORE_NAME);
		return r;
	}

	private Map<String, TaskModel> getTaskModelResourceMap() {
		@SuppressWarnings("unchecked")
		Map<String, TaskModel> m = (Map<String, TaskModel>) getLocalStore().get(TASK_MODEL_NAMESPACE);
		return m;
	}

	private void createResourceMaps() {
		getLocalStore().put(TASK_MODEL_NAMESPACE, new HashMap<>());
	}

}
