package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.infra.cloud.AbstractCloudEnvironmentDelegate;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class CloudEnvironmentSPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.CLOUD_ENVIRONMENT;
	}

	@Override
	public Class<? extends Resource> classType() {
		return CloudEnvironment.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractCloudEnvironmentDelegate.class;
	}
}
