package com.re.paas.internal.cloud;

import java.net.InetAddress;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.infra.cloud.Tags;
import com.re.paas.api.networking.AddressResolver;
import com.re.paas.internal.networking.IPAddresses;

public interface CloudEnvironmentAdapter extends CloudEnvironment {

	@Override
	default String clusterName() {
		return getInstanceTags().get(Tags.CLUSTER_NAME_TAG);
	}
	
	@Override
	default Integer httpPort() {
		return Integer.parseInt(getInstanceTags().get(Tags.HTTP_PORT_TAG));
	}
	
	@Override
	default Integer httpsPort() {
		return Integer.parseInt(getInstanceTags().get(Tags.HTTPS_PORT_TAG));
	}
	
	@Override
	default InetAddress httpHost() {
		return AddressResolver.get().httpHost();
	}
	
	
	@Override
	default InetAddress clusteringHost() {
		return AddressResolver.get().clusteringHost();
	}

	default Integer clusteringPort() {
		return Integer.parseInt(getInstanceTags().get(Tags.CLUSTERING_PORT_TAG));
	}
	

	default InetAddress wkaHost() {
		return IPAddresses.getAddress(getInstanceTags().get(Tags.WKA_HOST_TAG));
	}

	@Override
	default Integer wkaPort() {
		return clusteringPort();
	}

	default Boolean enabled() {
		return false;
	}

	@BlockerTodo
	default Boolean applies() {
		return true;
	}

	default ClusterCredentials credentials() {

		String accessKey = getInstanceTags().get(Tags.CLUSTER_ACCESS_KEY_TAG);
		String secretKey = getInstanceTags().get(Tags.CLUSTER_SECRET_KEY_TAG);

		return new ClusterCredentials(accessKey, secretKey);
	}


	public static String getResourceName() {
		return CloudEnvironment.get().getInstanceTags().get(Tags.RESOURCE_NAME_TAG);
	}

}
