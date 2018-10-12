package com.re.paas.internal.classes.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This ReentrantLock make use of a single condition instance, with the goal of
 * having a single wait-set for this lock object
 * 
 * @author Tony
 *
 */
public class SingleConditionReentrantLock extends ReentrantLock {

	private Condition defaultCondition;
	private static final long serialVersionUID = 1L;

	public SingleConditionReentrantLock() {
		this(false);
	}
	
	public SingleConditionReentrantLock(boolean fair) {
		super(fair);
	}
	
	public Condition getCondition() {

		if (defaultCondition == null) {
			defaultCondition = super.newCondition();
		}

		return defaultCondition;
	}
	
	public boolean hasWaiters() {
		return getWaitQueueLength(defaultCondition) > 0;
	}
}
