package com.re.paas.api.runtime.spi;

import java.util.ArrayList;
import java.util.List;

public enum ResourcesInitResult {

	SUCCESS,

	PARTIAL_SUCCESS;

	private List<ResourceError> errors = new ArrayList<>();
	private Integer count;
	
	public Integer getCount() {
		return count;
	}
	
	public ResourcesInitResult setCount(Integer count) {
		this.count = count;
		return this;
	}

	public ResourcesInitResult addError(Class<? extends AbstractResource> clazz, String message) {
		this.errors.add(new ResourceError(message, clazz));
		return this;
	}
	
	public ResourcesInitResult setErrors(List<ResourceError> errors) {
		this.errors = errors;
		return this;
	}
	
	public List<ResourceError> getErrors() {
		return errors;
	}

	public static class ResourceError {

		private final String errorMessage;
		private final Class<? extends AbstractResource> culprit;

		ResourceError(String errorMessage, Class<? extends AbstractResource> culprit) {
			super();
			this.errorMessage = errorMessage;
			this.culprit = culprit;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

		public Class<? extends AbstractResource> getCulprit() {
			return culprit;
		}

	}

}
