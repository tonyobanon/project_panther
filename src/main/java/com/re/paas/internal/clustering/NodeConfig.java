package com.re.paas.internal.clustering;

import java.io.File;

import com.re.paas.internal.classes.Configuration;
import com.re.paas.internal.clustering.classes.Utils;

public class NodeConfig extends Configuration {
	
	private static final String NODE_CONFIG_FILE_PATH = "application_config" + File.separator + "node-config.properties";

	public static final String DATA_GRID_ALLOCATED_MEMORY = "datagrid.allocatedMemory";

	public static final String DATA_GRID_ENABLE_SNAPSHOTS = "datagrid.enableSnapshots";
	public static final String DATA_GRID_SNAPHOT_INTERVAL = "datagrid.snapshotInterval";
	public static final String DATA_GRID_SNAPSHOTS_PATH = "datagrid.snapshotsPath";

	public static final String LOG_VERBOSE_MODE = "logs.verboseMode";

	private static Configuration DEFAULT_INSTANCE = new NodeConfig();
	
	public static Configuration getInstance() {
		return DEFAULT_INSTANCE;
	}
	
	@Override
	protected String filePath() {
		return Utils.getAppBaseDir() + NODE_CONFIG_FILE_PATH;
	}
}
