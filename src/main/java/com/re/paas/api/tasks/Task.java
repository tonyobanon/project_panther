package com.re.paas.api.tasks;

import java.io.Serializable;
import java.util.Date;

import com.re.paas.api.classes.TaskExecutionOutcome;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public abstract class Task extends AbstractResource implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_GROUP = "default";
	
	public Task() {
		super(SpiType.SCHEDULED_TASK);
	}

	public abstract String id();
	
	public String group() {
		return DEFAULT_GROUP;
	}
	
	public Date startDate() {
		return null;
	}
	
	/**
	 * This should be a cron expression to match a given interval for which the task
	 * should be executed
	 * 
	 * @return
	 */
	public abstract String interval();
	
	public abstract TaskExecutionOutcome call();
	
	public Integer maxExecutions() {
		return -1;
	}
	
	public Date stopDate() {
		return null;
	}
	
	public abstract Affinity affinity();
}
