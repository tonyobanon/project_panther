package com.re.paas.internal.clustering;

import java.io.File;

import com.re.paas.internal.classes.Configuration;
import com.re.paas.internal.clustering.classes.Utils;

public class MasterNodeConfig extends Configuration {

	private static final String MASTER_NODE_CONFIG_FILE_PATH = "application_config" + File.separator + "master-node-config.properties";
	
	public static final String DATA_GRID_PARTITION_SIZE = "datagrid.partitionSize";
	public static final String DATA_GRID_BASE_ITEM_SIZE = "datagrid.baseItemSize";

	public static final String AUTO_SCALING_FACTORS = "autoScalingFactors";
	public static final String AUTO_SCALING_BASELINE = "autoScalingBaseline";

	public static final String REGION = "region";

	public static final String PRIVATE_SUBNET_ID = "privateSubnetId";

	public static final String AZURE_SUBSCRIPTION = "-azure-subscription";
	public static final String AZURE_CLIENT = "-azure-client";
	public static final String AZURE_KEY = "-azure-key";
	public static final String AZURE_MANAGEMENT_URI = "-azure-managementURI";
	public static final String AZURE_BASE_URL = "-azure-baseURL";
	public static final String AZURE_AUTH_URL = "-azure-authURL";
	public static final String AZURE_GRAPH_URL = "-azure-graphURL";
	public static final String AZURE_RESOURCE_GROUP = "-azure-resourceGroup";
	public static final String AZURE_VIRTUAL_MACHINE_SIZE_TYPE = "-azure-virtualMachineSizeType";

	public static final String AWS_ACCESS_KEY = "-aws-accessKey";
	public static final String AWS_SECRET_KEY = "-aws-secretKey";
	public static final String AWS_INSTANCE_TYPE = "-aws-instanceType";

	private static Configuration DEFAULT_INSTANCE = new MasterNodeConfig();
	
	public static Configuration getInstance() {
		return DEFAULT_INSTANCE;
	}
	
	@Override
	protected String filePath() {
		return Utils.getAppBaseDir() + MASTER_NODE_CONFIG_FILE_PATH;
	}
}
