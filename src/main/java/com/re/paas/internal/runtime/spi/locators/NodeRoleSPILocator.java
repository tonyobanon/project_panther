package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.roles.AbstractNodeRoleDelegate;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class NodeRoleSPILocator extends BaseSPILocator {
	
	@Override
	public SpiType spiType() {
		return SpiType.NODE_ROLE;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return AbstractRole.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractNodeRoleDelegate.class;
	}
	
}
