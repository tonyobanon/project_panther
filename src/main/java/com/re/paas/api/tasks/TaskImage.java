package com.re.paas.api.tasks;

import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.TaskExecutionOutcome;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.internal.classes.TaskInterval;

public abstract class TaskImage extends AbstractResource implements Cloneable {
	
	private Map<String, String> parameters;
	
	public TaskImage() {
		super(SpiType.TASK_MODEL);
	}
	
	public static AbstractTaskModelDelegate getDelegate() {
		return Singleton.get(AbstractTaskModelDelegate.class);
	}
	
	public abstract String name();
	
	public abstract ClientRBRef title();
	
	public abstract List<AbstractField> fields();
	
	public Map<String, String> getParameters() {
		return parameters;
	}

	public TaskImage setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
		return this;
	}

	public abstract TaskExecutionOutcome call();
	
	public abstract TaskInterval interval();
	
}
