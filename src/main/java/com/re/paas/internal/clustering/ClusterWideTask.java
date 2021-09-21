package com.re.paas.internal.clustering;

import java.io.Serializable;

import com.re.paas.api.runtime.Invokable;
import com.re.paas.internal.classes.BooleanSupplier;

public class ClusterWideTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Invokable task;
	private final BooleanSupplier predicate;
	private final Long intervalInSecs;
	private final Long initialDelay;
	
	public ClusterWideTask(Invokable task, BooleanSupplier predicate, Long intervalInSecs, Long initialDelay) {
		this.task = task;
		this.predicate = predicate;
		this.intervalInSecs = intervalInSecs;
		this.initialDelay = initialDelay;
	}

	public Invokable getTask() {
		return task;
	}

	public BooleanSupplier getPredicate() {
		return predicate;
	}

	public Long getIntervalInSecs() {
		return intervalInSecs;
	}
	
	public Long getInitialDelay() {
		return initialDelay;
	}
}
