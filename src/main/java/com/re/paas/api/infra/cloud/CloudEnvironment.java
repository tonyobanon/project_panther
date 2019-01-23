package com.re.paas.api.infra.cloud;

import java.net.InetAddress;
import java.util.Map;

import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.designpatterns.Singleton;

public interface CloudEnvironment {

	String id();

	Boolean enabled();

	Boolean applies();

	Boolean isProduction();

	String title();
	
	
	String clusterName();
	
	Integer httpPort();
	
	Integer httpsPort();
	
	InetAddress httpHost();

	Integer clusteringPort();

	InetAddress clusteringHost();
	

	Boolean canAutoScale();

	InetAddress wkaHost();
	
	Integer wkaPort();

	Map<String, String> getInstanceTags();

	ClusterCredentials credentials();

	AbstractProviderHandler providerDelegate();

	static AbstractCloudEnvironmentDelegate getDelegate() {
		return Singleton.get(AbstractCloudEnvironmentDelegate.class);
	}

	static CloudEnvironment get() {
		return getDelegate().getInstance();
	}
}
