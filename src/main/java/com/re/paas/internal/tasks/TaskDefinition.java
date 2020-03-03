package com.re.paas.internal.tasks;

import java.time.Instant;

import com.re.paas.api.tasks.Task;

public class TaskDefinition {

	private final Task task;
	private final Boolean isDeletable;
	private final String modelName;

	private Instant dateAdded;
	private Instant lastExecutionTime;
	private Instant nextExecutionTime;

	public TaskDefinition(Task task, Boolean isDeletable, String modelName) {
		this.task = task;
		this.isDeletable = isDeletable;
		this.modelName = modelName;
	}

	public Task getTask() {
		return task;
	}

	public Boolean getIsDeletable() {
		return isDeletable;
	}

	public String getModelName() {
		return modelName;
	}

	public Instant getLastExecutionTime() {
		return lastExecutionTime;
	}

	TaskDefinition setLastExecutionTime(Instant lastExecutionTime) {
		this.lastExecutionTime = lastExecutionTime;
		return this;
	}

	public Instant getNextExecutionTime() {
		return nextExecutionTime;
	}

	TaskDefinition setNextExecutionTime(Instant nextExecutionTime) {
		this.nextExecutionTime = nextExecutionTime;
		return this;
	}

	public Instant getDateAdded() {
		return dateAdded;
	}

	TaskDefinition setDateAdded(Instant dateAdded) {
		this.dateAdded = dateAdded;
		return this;
	}
}
