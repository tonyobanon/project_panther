package com.re.paas.internal.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ExecutorFactoryStats;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.classes.ThreadContext;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.ComputeException;
import com.re.paas.api.runtime.ComputeQuotaExceededEvent;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ExecutorFactoryConfig;
import com.re.paas.api.runtime.ExternalContext;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.runtime.ParameterizedInvokable;
import com.re.paas.api.runtime.RuntimeIdentity;
import com.re.paas.api.runtime.UnknownIdentityException;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.api.utils.Utils;

@BlockerTodo("In this class, find alternative to HashMap and ArrayList for high performance")
public class ExecutorFactoryImpl extends ExecutorFactory {

	private static final int defaultExponent = 5;
	private static final int executorCountCheckInterval = 5000;

	private int exponent = defaultExponent;

	private final Logger LOG;

	private final String name;

	private final List<String> used;
	private final List<String> free;

	private final ExecutorFactoryConfig config;

	private final Map<String, ExecutorService> executorPool;

	private boolean isUpgradable = true;
	private ScheduledExecutorService upgradeScheduler;

	private Runnable shutdownFuture;
	private boolean isShutdown = false;

	public ExecutorFactoryImpl(String name, ExecutorFactoryConfig config) {

		this.LOG = LoggerFactory.get().getLog().setNamespace(ExecutorFactoryImpl.class, name);

		this.name = name;
		this.config = config;

		this.used = Collections.synchronizedList(new ArrayList<>());
		this.free = Collections.synchronizedList(new ArrayList<>());

		this.executorPool = Collections.synchronizedMap(new HashMap<>());

		this.upgradePool();
		this.scheduleUpgrade();
	}

	private void scheduleUpgrade() {

		upgradeScheduler = Executors.newScheduledThreadPool(1);

		upgradeScheduler.scheduleAtFixedRate(() -> {

			if (!isFactoryAvailable() || !isUpgradable) {

				exponent = -1;
				upgradeScheduler.shutdown();
				return;
			}

			if (needsUpgrade()) {

				if (canUpgrade()) {
					this.upgradePool();
				}

				if (!isUpgradable) {

					ComputeQuotaExceededEvent evt = new ComputeQuotaExceededEvent();
					AbstractEventDelegate.getInstance().dispatch(evt, true);
				}

			}

		}, 0, executorCountCheckInterval, TimeUnit.MILLISECONDS);
	}

	ExecutorFactoryConfig getConfig() {
		return this.config;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@BlockerTodo("Create mechanism to monitor the number of spawned threads per application..")
	@BlockerTodo("Enforce a timeout policy, so that calls to i.call() don't take forever..")
	@Override
	public <P, R> CompletableFuture<R> executeLocal(ParameterizedExecutable<P, R> executable) {

		if (free.isEmpty()) {
			Exceptions.throwRuntime(new ComputeException("No executor(s) available to run this task"));
		}

		// Get available instance

		String executorId = free.get(free.size() - 1);
		ExecutorService executor = executorPool.get(executorId);

		markAsUsed(executorId);

		CompletableFuture<R> r = new CompletableFuture<>();

		try {

			executor.submit(() -> {

				R result = executable.getFunction().apply(executable.getParameter());

				markAsFree(executorId);

				r.complete(result);
				return null;

			}).get();

		} catch (NullPointerException | InterruptedException | ExecutionException e) {
			Exceptions.throwRuntime(e);
		}

		return r;
	}

	private void markAsFree(String id) {
		this.free.add(id);
		this.used.remove(id);
	}

	private void markAsUsed(String id) {
		this.free.remove(id);
		this.used.add(id);
	}

	@Override
	public void shutdown() {

		if (shutdownFuture != null) {
			return;
		}

		shutdownFuture = () -> {

			LOG.debug("Shutting down executor pool..");

			Long len = (long) (used.size() + free.size());

			List<String> instances = new ArrayList<>(len.intValue());
			instances.addAll(free);
			instances.addAll(used);

			free.clear();
			used.clear();

			instances.forEach(id -> {
				executorPool.get(id).shutdown();
			});

			executorPool.clear();

			isUpgradable = false;

			shutdownFuture = null;
			isShutdown = true;
		};

		shutdownFuture.run();
	}

	@Override
	public boolean isShutdown() {
		return isShutdown;
	}

	/**
	 * This registers a new instance in this pool
	 */
	private void addInstance() {

		String id = Utils.newShortRandom();

		Permissions.bypass.set(true);

		ExecutorService e = Executors.newSingleThreadExecutor();

		// Add a no-op task to force thread spawning
		e.execute(() -> {
			
		});

		Permissions.bypass.set(false);

		executorPool.put(id, e);

		free.add(id);
	}

	private void upgradePool() {

		long executorCount = exponent;

		LOG.debug("Adding " + executorCount + " executors(s) to the pool");

		for (int i = 0; i < executorCount; i++) {
			addInstance();
		}

		this.exponent += exponent;
	}

	@BlockerTodo("Implement this")
	private boolean downgradePool() {
		return false;
	}

	@Override
	public ExecutorFactoryStats getStatistics() {
		return new ExecutorFactoryStats().setExponent(exponent).setTotalFree(free.size()).setTotalUsed(used.size())
				.setMaxThreads(config.getMaxThreads()).setThreadCount(executorPool.size());
	}

	private long getMaxExecutorsAllowed() {
		return config.getMaxThreads();
	}

	private boolean needsUpgrade() {
		return free.size() <= (int) 0.05 * exponent;
	}

	private boolean canUpgrade() {

		if (!isUpgradable) {
			return false;
		}

		int executorSize = executorPool.size();

		if (executorSize >= getMaxExecutorsAllowed()) {
			isUpgradable = false;
			return false;
		}

		boolean isUpgradable = this.isUpgradable;
		int exponent = this.exponent;

		for (; exponent > 0; exponent = (int) (exponent / 1.5)) {

			if (executorSize + exponent + 1 <= getMaxExecutorsAllowed()) {
				break;
			} else {
				isUpgradable = false;
			}
		}

		this.exponent = exponent;
		this.isUpgradable = isUpgradable;

		return exponent >= 1;
	}

	private boolean isFactoryAvailable() {
		return shutdownFuture == null && !isShutdown;
	}

	@Override
	public <P, R> ParameterizedExecutable<P, R> buildFunction(ParameterizedInvokable<P, R> task, P parameter, Affinity affinity) {
		
		String appId = ThreadContext.getAppId();
		
		if (appId == null && RuntimeIdentity.isExternalContext()) {
			throw new UnknownIdentityException();
		}
		
		return buildFunction(new ObjectWrapper<ClassLoader>(task.getClass().getClassLoader()), task, parameter, new ExternalContext(appId, false, affinity));
	}

	@Override
	public <P, R> ParameterizedExecutable<P, R> buildFunction(ObjectWrapper<ClassLoader> cl,
			ParameterizedInvokable<P, R> task, P parameter, ExternalContext ctx) {
		
		if (cl.get() == null) {
			cl.set(task.getClass().getClassLoader());
		}
		
		String classloader = ClassLoaders.getId(cl.get());
		
		@SuppressWarnings("unchecked")
		Function<P, R> function = (Function<P, R> & Serializable) (p) -> {

			LOG.debug("Executing function: " + task + " by " + classloader);

			ThreadContext.newRequestContext(ctx.getAppId(), ctx.getIsWebRequest());
			
			
			Thread thread = Thread.currentThread();

			// Set thread context loader
			Permissions.bypass.set(true);
			thread.setContextClassLoader(ClassLoaders.getClassLoader(classloader));
			Permissions.bypass.set(false);

			// Initialize permission set for this thread
			Permissions.init();

			// Execute task
			R result = task.apply(p);

			// Clear permission set for this thread
			Permissions.clear();

			// Reset thread context loader
			Permissions.bypass.set(true);
			thread.setContextClassLoader(null);
			Permissions.bypass.set(false);

			
			ThreadContext.clear();
			
			return result;
		};

		return new ParameterizedExecutable<P, R>(function, (P) parameter, ctx.getAffinity());
	}

}
