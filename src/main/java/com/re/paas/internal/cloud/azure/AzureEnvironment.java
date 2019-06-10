package com.re.paas.internal.cloud.azure;

import java.util.Map;

import com.re.paas.api.infra.cloud.AbstractProviderHandler;
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
	public Boolean isClustered() {
		return true;
	}

	@Override
	public Map<String, String> getInstanceTags() {
		return AzureHelper.getInstanceTags();
	}

	@Override
	public AbstractProviderHandler providerDelegate() { 
		return new AzureHandler();
	}
	
}
