package com.re.paas.internal.clustering;

import java.io.File;
import java.util.regex.Pattern;

import com.re.paas.internal.classes.Configuration;
import com.re.paas.internal.clustering.classes.Utils;

public class ClusterConfig extends Configuration {
	
	private static final String CLUSTER_CONFIG_FILE_PATH = "application_config" + File.separator + "cluster-config.properties";
	
	public static Pattern CLUSTER_NAME_PATTERN = Pattern.compile("\\A([a-zA-Z]+[-]*[a-zA-Z]+)+\\z");
	public static final String CLUSTER_NAME = "clusterName";

	public static final String HTTP_PORT = "httpPort";
	
	public static final String CLUSTERING_PORT = "clusteringPort";

	public static final String SIGNATURE = "signature";

	private static Configuration DEFAULT_INSTANCE = new ClusterConfig();
	
	public static Configuration getInstance() {
		return DEFAULT_INSTANCE;
	}
	
	@Override
	protected String filePath() {
		return Utils.getAppBaseDir() + CLUSTER_CONFIG_FILE_PATH;
	}
}
