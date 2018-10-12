package com.re.paas.internal.cloud;

import java.net.InetAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.cache.CacheFactory;
import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.cloud.Tags;
import com.re.paas.api.clustering.AbstractMasterNodeRole;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.ClusterCredentials;
import com.re.paas.api.databases.DatabaseAdapter;
import com.re.paas.api.filesystems.FileSystemAdapter;
import com.re.paas.api.networking.AddressResolver;
import com.re.paas.internal.cache.DefaultCacheFactory;
import com.re.paas.internal.clustering.ClusterConfig;
import com.re.paas.internal.databases.Database;
import com.re.paas.internal.networking.IPAddresses;

public interface CloudEnvironmentAdapter extends CloudEnvironment {
	
	@Override
	default InetAddress clusteringHost() {
		return AddressResolver.get().clusteringHost();
	}
	
	default Integer clusteringPort() {
		return ClusterConfig.getInstance().getInteger(ClusterConfig.CLUSTERING_PORT);
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

		AbstractMasterNodeRole masterRole = NodeRole.getDelegate().getMasterRole();

		if (masterRole != null && masterRole.getClusterCredentials() != null) {
			return masterRole.getClusterCredentials();
		} else {

			String accessKey = getInstanceTags().get(ClusterCredentials.CLUSTER_ACCESS_KEY_TAG);
			String secretKey = getInstanceTags().get(ClusterCredentials.CLUSTER_SECRET_KEY_TAG);

			return new ClusterCredentials(accessKey, secretKey);
		}
	}
	

	@Override
	default CacheFactory<String, String> getCacheFactory() {
		return DefaultCacheFactory.getInstance();
	}
	
	@Override
	default Connection database() {
		return Database.get();
	}

	@Override
	default List<DatabaseAdapter> databaseAdapters() {
		return new ArrayList<>();
	}
	
	@Override
	default List<FileSystemAdapter> fileSystemAdapters() {
		return new ArrayList<>();
	}
	
	public static DatabaseAdapter getDatabaseAdapter(String name) {
		return CloudEnvironment.getDelegate().getDbAdaptersMap().get(name);
	}
	
	public static FileSystemAdapter getFileSystemAdapter(String name) {
		return CloudEnvironment.getDelegate().getFsAdaptersMap().get(name);
	}
	
	public static String getResourceName() {
		return CloudEnvironment.get().getInstanceTags().get(Tags.RESOURCE_NAME_TAG);
	}
	
}
