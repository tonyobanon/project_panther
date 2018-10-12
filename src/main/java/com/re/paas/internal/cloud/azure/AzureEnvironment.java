package com.re.paas.internal.cloud.azure;

import java.util.Map;

import com.re.paas.api.cloud.AutoScaleDelegate;
import com.re.paas.internal.cloud.CloudEnvironmentAdapter;

public class AzureEnvironment implements CloudEnvironmentAdapter {

	@Override
	public String id() {
		return "azure";
	}

	@Override
	public Boolean isProduction() {
		return true;
	}

	@Override
	public String title() {
		return "Azure";
	}

	@Override
	public Boolean canAutoScale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getInstanceTags() {
		return AzureHelper.getInstanceTags();
	}

	@Override
	public AutoScaleDelegate autoScaleDelegate() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
