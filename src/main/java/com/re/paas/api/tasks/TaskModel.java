package com.re.paas.api.tasks;

import java.util.List;
import java.util.Map;

import com.re.paas.api.Singleton;
import com.re.paas.api.forms.Section;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public abstract class TaskModel extends AbstractResource {

	public static AbstractTaskDelegate getDelegate() {
		return Singleton.get(AbstractTaskDelegate.class);
	}
	
	public TaskModel() {
		super(SpiType.SCHEDULED_TASK);
	}

	public abstract String name();

	public abstract String title();

	public abstract List<Section> fields();

	public abstract Task build(Map<String, Object> parameters);
	
	public Map<String, Object> defaultParameters() {
		return null;
	}
}
