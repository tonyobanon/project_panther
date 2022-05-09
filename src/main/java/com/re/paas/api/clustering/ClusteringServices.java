package com.re.paas.api.clustering;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.Singleton;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.internal.clustering.ClusterWideTask;

public interface ClusteringServices {

	public static ClusteringServices get() {
		return Singleton.get(ClusteringServices.class);
	}

	@SecureMethod
	CompletableFuture<Void> start() throws IOException;

	@SecureMethod
	Object getMultimapCacheManager();

	@SecureMethod
	Object getCacheManager();

	@SecureMethod
	CompletableFuture<Collection<ClusterWideTask>> getClusterWideTasks();

	void addClusterWideTask(ClusterWideTask task);

	void removeClusterWideTask(String name);

	/**
	 * Note: It is guaranteed that any given node in the cluster can only be elected
	 * as the executioner once in it's lifetime
	 */
	@SecureMethod
	void assumeExecutioner();

	boolean isExecutioner();

	default <T, R> CompletableFuture<R> execute(ParameterizedExecutable<T, R> task) {
		return execute(task, true);
	}

	<T, R> CompletableFuture<R> execute(ParameterizedExecutable<T, R> task, boolean wait);

}
