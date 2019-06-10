package com.re.paas.api.infra.cloud;

import java.net.InetAddress;
import java.util.Map;

import com.re.paas.api.annotations.Final;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.MethodMeta;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;

public interface CloudEnvironment extends Resource {

	String id();

	Boolean enabled();

	@MethodMeta
	Boolean applies();

	Boolean isProduction();

	String title();
	
	
	String clusterName();
	
	Integer httpPort();
	
	Integer httpsPort();
	
	InetAddress httpHost();

	Integer clusteringPort();

	InetAddress clusteringHost();

	Boolean isClustered();

	InetAddress wkaHost();
	
	Integer wkaPort();

	@MethodMeta
	Map<String, String> getInstanceTags();

	@MethodMeta
	ClusterCredentials credentials();

	@MethodMeta
	AbstractProviderHandler providerDelegate();

	static AbstractCloudEnvironmentDelegate getDelegate() {
		return Singleton.get(AbstractCloudEnvironmentDelegate.class);
	}

	static CloudEnvironment get() {
		return getDelegate().getInstance();
	}
	
	@Override
	@Final
	default SpiType getSpiType() {
		return SpiType.CLOUD_ENVIRONMENT;
	}
}
