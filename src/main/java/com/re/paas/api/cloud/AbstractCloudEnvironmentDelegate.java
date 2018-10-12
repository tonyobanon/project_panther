package com.re.paas.api.cloud;

import java.util.List;
import java.util.Map;

import com.re.paas.api.databases.DatabaseAdapter;
import com.re.paas.api.filesystems.FileSystemAdapter;
import com.re.paas.api.spi.SpiDelegate;

public abstract class AbstractCloudEnvironmentDelegate extends SpiDelegate<CloudEnvironment> {

	public abstract CloudEnvironment getInstance();

	public abstract List<CloudEnvironment> getInstances();
	
	public abstract Map<String, FileSystemAdapter> getFsAdaptersMap();
	
	public abstract Map<String, DatabaseAdapter> getDbAdaptersMap();
	
}
