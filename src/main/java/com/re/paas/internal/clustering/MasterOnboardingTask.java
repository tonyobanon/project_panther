package com.re.paas.internal.clustering;

import java.io.Serializable;
import java.util.function.BooleanSupplier;

import com.re.paas.api.runtime.Invokable;

public class MasterOnboardingTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Invokable task;
	private final BooleanSupplier predicate;
	private final Long initialExecutionDelay;
	
	public MasterOnboardingTask(Invokable task, BooleanSupplier predicate, Long initialExecutionDelay) {
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
