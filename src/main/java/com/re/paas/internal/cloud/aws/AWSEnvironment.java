package com.re.paas.internal.cloud.aws;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.re.paas.api.cloud.AutoScaleDelegate;
import com.re.paas.api.databases.DatabaseAdapter;
import com.re.paas.internal.cloud.CloudEnvironmentAdapter;
import com.re.paas.internal.databases.dynamodb.DynamoDBAdapter;

public class AWSEnvironment implements CloudEnvironmentAdapter {

	@Override
	public String id() {
		return null;
	}

	@Override
	public Boolean isProduction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String title() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean canAutoScale() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * In AWS, we use user-data to store our "instance tags"
	 * */	
	@Override
	public Map<String, String> getInstanceTags() {
		return AWSHelper.getUserData();
	}

	@Override
	public AutoScaleDelegate autoScaleDelegate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<DatabaseAdapter> databaseAdapters() {
		return ImmutableList.of(new DynamoDBAdapter());
	}
}
