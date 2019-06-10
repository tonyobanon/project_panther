package com.re.paas.internal.cloud.aws;

import java.util.Map;

import com.re.paas.api.infra.cloud.AbstractProviderHandler;
import com.re.paas.internal.cloud.CloudEnvironmentAdapter;

public class AWSEnvironment implements CloudEnvironmentAdapter {

	@Override
	public String id() {
		return "aws";
	}

	@Override
	public Boolean isProduction() {
		return true;
	}

	@Override
	public String title() {
		return "Amazon Web Services";
	}

	@Override
	public Boolean isClustered() {
		return true;
	}
	
	/**
	 * In AWS, we use user-data to store our "instance tags"
	 * */	
	@Override
	public Map<String, String> getInstanceTags() {
		return AWSHelper.getUserData();
	}

	@Override
	public AbstractProviderHandler providerDelegate() {
		return new AwsHandler();
	}
}
