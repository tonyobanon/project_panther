package com.re.paas.api.runtime;

import java.util.concurrent.CompletableFuture;

import com.re.paas.api.concurrency.ExecutorFactoryStats;
import com.re.paas.api.designpatterns.Factory;
import com.re.paas.api.runtime.spi.SpiBase;
import com.re.paas.internal.runtime.security.Secure;

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

	@Secure
	public abstract void onComputeQuotaExceeded(ComputeQuotaExceededEvent evt);

	public abstract String getName();

	public abstract <R> CompletableFuture<R> execute(Invokable<R> task);

	@Secure
	public abstract void shutdown();

	public abstract boolean isShutdown();

	@Secure
	public abstract void upgradePool();

	@Secure
	public abstract boolean downgradePool();

	public abstract ExecutorFactoryStats getStatistics();

	public abstract boolean isUpgradable();
}