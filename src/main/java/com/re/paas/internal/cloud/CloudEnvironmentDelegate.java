package com.re.paas.internal.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.cloud.AbstractCloudEnvironmentDelegate;
import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.databases.DatabaseAdapter;
import com.re.paas.api.filesystems.FileSystemAdapter;
import com.re.paas.api.utils.ClassUtils;

public class CloudEnvironmentDelegate extends AbstractCloudEnvironmentDelegate {

	private static final String FS_ADAPTERS_NAMESPACE = "fs_adapters";
	private static final String DB_ADAPTERS_NAMESPACE = "db_adapters";

	private static CloudEnvironment instance;

	@Override
	public void init() {
		
		createResourceMaps();
		
		forEach(c -> {

			CloudEnvironment e = ClassUtils.createInstance(c);

			registerAdapters(e);

			if (get(e.id()) != null) {
				throw new RuntimeException("Duplicate CloudEnvironment definitions exists with id: " + e.id());
			}
			set(e.id(), e);
		});
	}

	private void registerAdapters(CloudEnvironment e) {

		Map<String, DatabaseAdapter> dbAdapters = getDbAdaptersMap();
		
		e.databaseAdapters().forEach(dbAdapter -> {
			if(dbAdapters.containsKey(dbAdapter.name())) {
				throw new RuntimeException("Duplicate DatabaseAdapter definitions exists with name: " + dbAdapter.name());
			}
			dbAdapters.put(dbAdapter.name(), dbAdapter);
		});
		
		Map<String, FileSystemAdapter> fsAdapters = getFsAdaptersMap();
		
		e.fileSystemAdapters().forEach(fsAdapter -> {
			if(fsAdapters.containsKey(fsAdapter.name())) {
				throw new RuntimeException("Duplicate FileSystemAdapter definitions exists with name: " + fsAdapter.name());
			}
			fsAdapters.put(fsAdapter.name(), fsAdapter);
		});
	}

	@Override
	public CloudEnvironment getInstance() {

		if (instance != null) {
			return instance;
		}

		List<CloudEnvironment> instances = getInstances();

		for (CloudEnvironment e : instances) {
			if (e.applies()) {
				instance = e;
				return instance;
			}
		}

		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, FileSystemAdapter> getFsAdaptersMap() {
		return (Map<String, FileSystemAdapter>) get(FS_ADAPTERS_NAMESPACE);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, DatabaseAdapter> getDbAdaptersMap() {
		return (Map<String, DatabaseAdapter>) get(DB_ADAPTERS_NAMESPACE);
	}

	@Override
	public List<CloudEnvironment> getInstances() {

		Map<Object, Object> m = getAll();
		List<CloudEnvironment> r = new ArrayList<CloudEnvironment>();

		m.forEach((k, v) -> {
			CloudEnvironment env = (CloudEnvironment) v;
			if (env.enabled()) {
				r.add(env);
			}
		});
		return r;
	}
	
	private void createResourceMaps() {
		set(FS_ADAPTERS_NAMESPACE, new HashMap<>());
		set(DB_ADAPTERS_NAMESPACE, new HashMap<>());
	}
}
