package com.re.paas.api.runtime;

import java.io.Serializable;

import com.re.paas.api.tasks.Affinity;

public class ExternalContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String appId;
	private final Boolean isWebRequest;
	private final Affinity affinity;
	
	public ExternalContext(String appId, Boolean isWebRequest, Affinity affinity) {
		this.appId = appId;
		this.isWebRequest = isWebRequest;
		this.affinity = affinity;
	}

	public String getAppId() {
		return appId;
	}

	public Boolean getIsWebRequest() {
		return isWebRequest;
	}

	public Affinity getAffinity() {
		return affinity;
	}

}
