package com.re.paas.api.clustering.classes;

import java.io.Serializable;

public class ClusterCredentials implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String accessKey;
	private final String secretKey;

	public ClusterCredentials(String accessKey, String secretKey) {
		this.accessKey = accessKey;
		this.secretKey = secretKey;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

}
