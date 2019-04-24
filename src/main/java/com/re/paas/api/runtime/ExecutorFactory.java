package com.re.paas.api.runtime;

import java.util.concurrent.CompletableFuture;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.concurrency.ExecutorFactoryStats;
import com.re.paas.api.designpatterns.Factory;
import com.re.paas.api.runtime.spi.SpiBase;

public abstract class ExecutorFactory {

	public static final Integer MAX_THREAD_COUNT = 12000;
	private static ExecutorFactory instance;

	public static ExecutorFactory get() {
		return instance;
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

	@ProtectionContext
	public abstract void onComputeQuotaExceeded(ComputeQuotaExceededEvent evt);

	public abstract String getName();

	public abstract <R, T> CompletableFuture<T> execute(Invokable<R> task);

	@ProtectionContext
	public abstract void shutdown();

	public abstract boolean isShutdown();

	@ProtectionContext
	public abstract void upgradePool();

	@ProtectionContext
	public abstract boolean downgradePool();

	public abstract ExecutorFactoryStats getStatistics();

	public abstract boolean isUpgradable();
}