package com.re.paas.internal.clustering;

import java.io.Serializable;

import com.re.paas.api.runtime.Invokable;

public class ClusterWideTask implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	private final Invokable task;
	
	public ClusterWideTask(String name, Invokable task) {
		this.name = name;
		this.task = task;
	}

	public String getName() {
		return name;
	}
	
	public Invokable getTask() {
		return task;
	}
	
}
