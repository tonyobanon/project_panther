package com.re.paas.api.clustering.classes;

import java.io.Serializable;

public class ClusterCredentials implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public final static String CLUSTER_ACCESS_KEY_TAG = "ce_cluster_access_key_tag";
	public final static String CLUSTER_SECRET_KEY_TAG = "ce_cluster_secret_key_tag";

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
