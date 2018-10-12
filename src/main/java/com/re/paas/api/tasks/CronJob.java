package com.re.paas.api.tasks;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.re.paas.api.classes.TaskExecutionOutcome;

public abstract class CronJob implements Callable<TaskExecutionOutcome>, Serializable {

	private static final long serialVersionUID = 1L;
	
	public static CronJob get(Callable<TaskExecutionOutcome> o) {
		return new CronJob() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public TaskExecutionOutcome call() throws Exception {
				return o.call();
			}
		};
	}
	
}
