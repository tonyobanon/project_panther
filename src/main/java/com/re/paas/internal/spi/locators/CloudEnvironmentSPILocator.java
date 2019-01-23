package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.infra.cloud.AbstractCloudEnvironmentDelegate;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class CloudEnvironmentSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.CLOUD_ENVIRONMENT;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("Environment");
	}

	@Override
	public Class<?> classType() {
		return CloudEnvironment.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractCloudEnvironmentDelegate.class;
	}
}
