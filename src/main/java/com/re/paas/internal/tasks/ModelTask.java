package com.re.paas.internal.tasks;

import java.io.Serializable;
import java.util.Map;

public class ModelTask implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;
	private String modelName;
	private Map<String, String> parameters;

	public String getTitle() {
		return title;
	}

	public ModelTask setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getModelName() {
		return modelName;
	}

	public ModelTask setModelName(String modelName) {
		this.modelName = modelName;
		return this;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public ModelTask setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
		return this;
	}
}
