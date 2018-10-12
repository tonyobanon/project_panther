package com.re.paas.api.cloud;

import java.net.InetAddress;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.re.paas.api.cache.Cache;
import com.re.paas.api.cache.CacheFactory;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.databases.DatabaseAdapter;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.filesystems.FileSystemAdapter;

public interface CloudEnvironment {

	String id();

	Boolean enabled();

	Boolean applies();

	Boolean isProduction();

	String title();

	Integer clusteringPort();

	InetAddress clusteringHost();

	Boolean canAutoScale();

	InetAddress wkaHost();
	
	Integer wkaPort();

	Map<String, String> getInstanceTags();

	ClusterCredentials credentials();

	AutoScaleDelegate autoScaleDelegate();

	static AbstractCloudEnvironmentDelegate getDelegate() {
		return Singleton.get(AbstractCloudEnvironmentDelegate.class);
	}

	static CloudEnvironment get() {
		return getDelegate().getInstance();
	}

	CacheFactory<String, String> getCacheFactory();

	default Cache<String, String> getCache() {
		return getCacheFactory().get();
	}

	List<DatabaseAdapter> databaseAdapters();

	default Connection database() {
		return null;
	}
	
	List<FileSystemAdapter> fileSystemAdapters();
}
