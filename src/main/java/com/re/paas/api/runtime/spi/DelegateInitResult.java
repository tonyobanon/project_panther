package com.re.paas.api.runtime.spi;

import java.util.ArrayList;

import com.re.paas.api.adapters.AdapterType;

public enum DelegateInitResult {

	/**
	 * This indicates that the delegate started successfully, and all resources were
	 * loaded
	 */
	SUCCESS,

	/**
	 * This indicates that a system failure caused the delegate not to start
	 * successfully
	 */
	FAILURE,

	/**
	 * This indicates that the delegate started successfully, but one or more
	 * resource(s) could not be loaded
	 */
	PARTIAL_SUCCESS,

	PENDING_ADAPTER_CONFIGURATION;

	private Object meta;

	public Object getMeta() {
		return meta;
	}

	public DelegateInitResult setError(String errorMessage) {
		this.meta = errorMessage;
		return this;
	}

	public DelegateInitResult setType(AdapterType type) {
		this.meta = type;
		return this;
	}

	public DelegateInitResult addResourceError(Class<? extends AbstractResource> clazz, String errorMessage) {
		if (this.meta == null) {
			this.meta = new ArrayList<>();
		}

		@SuppressWarnings("unchecked")
		ArrayList<ResourceError> l = (ArrayList<ResourceError>) this.meta;
		l.add(new ResourceError(errorMessage, clazz));
		return this;
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
