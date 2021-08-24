package com.re.paas.api.runtime;

import java.util.concurrent.CompletableFuture;

import com.re.paas.api.Factory;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.classes.ExecutorFactoryStats;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.runtime.spi.SpiBase;
import com.re.paas.api.tasks.Affinity;

public abstract class ExecutorFactory {

	public static final Integer MAX_THREAD_COUNT = 12000;
	private static ExecutorFactory instance;

	public static ExecutorFactory get() {
		return instance;
	}

	public static ExecutorFactory create(ExecutorFactoryConfig config) {
		return create("default", config);
	}

	/**
	 * This is invoke when {@link SpiBase} is being started. Any call to this
	 * function in the future has no side effect
	 * 
	 * @param name
	 * @param config
	 * @return
	 */
	public static ExecutorFactory create(String name, ExecutorFactoryConfig config) {
		if (instance == null) {
			return instance = Factory.get(ExecutorFactory.class, name, config);
		}
		return instance;
	}

	public abstract String getName();
	
	public abstract <P, R> CompletableFuture<R> executeLocal(ParameterizedExecutable<P, R> e);

	@SecureMethod
	public abstract void shutdown();

	public abstract boolean isShutdown();

	public abstract ExecutorFactoryStats getStatistics();

	/**
	 * This is used to build a job. The resulting {@link ParameterizedExecutable}
	 * can be passed into any arbitrary job execution mechanism on this platform
	 * 
	 * @param task
	 * @return
	 */
	public abstract <P, R> ParameterizedExecutable<P, R> buildFunction(ParameterizedInvokable<P, R> task, P parameter, Affinity affinity);

	@PlatformInternal
	@SecureMethod
	public abstract <P, R> ParameterizedExecutable<P, R> buildFunction(ObjectWrapper<ClassLoader> cl, ParameterizedInvokable<P, R> task, P parameter, ExternalContext ctx);
	
}