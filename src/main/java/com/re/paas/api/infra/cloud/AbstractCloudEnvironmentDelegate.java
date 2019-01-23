package com.re.paas.api.infra.cloud;

import java.util.List;

import com.re.paas.api.spi.SpiDelegate;

public abstract class AbstractCloudEnvironmentDelegate extends SpiDelegate<CloudEnvironment> {

	public abstract CloudEnvironment getInstance();

	public abstract List<CloudEnvironment> getInstances();
	
}
