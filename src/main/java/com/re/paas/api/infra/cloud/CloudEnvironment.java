package com.re.paas.api.infra.cloud;

import java.net.InetAddress;
import java.util.Map;

import com.re.paas.api.annotations.Final;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.internal.runtime.security.Secure;

public interface CloudEnvironment extends Resource {

	String id();

	Boolean enabled();

	@Secure
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

	@Secure
	Map<String, String> getInstanceTags();

	@Secure
	ClusterCredentials credentials();

	@Secure
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
