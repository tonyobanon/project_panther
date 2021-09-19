package com.re.paas.internal.clustering;

import java.io.Serializable;
import java.util.function.BooleanSupplier;

import com.re.paas.api.runtime.Invokable;

public class ClusterWideTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Invokable task;
	private final BooleanSupplier predicate;
	private final Long initialExecutionDelay;
	
	public ClusterWideTask(Invokable task, BooleanSupplier predicate, Long initialExecutionDelay) {
		this.task = task;
		this.predicate = predicate;
		this.initialExecutionDelay = initialExecutionDelay;
	}

	public Invokable getTask() {
		return task;
	}

	public BooleanSupplier getPredicate() {
		return predicate;
	}

	public Long getInitialExecutionDelay() {
		return initialExecutionDelay;
	}
	
}
