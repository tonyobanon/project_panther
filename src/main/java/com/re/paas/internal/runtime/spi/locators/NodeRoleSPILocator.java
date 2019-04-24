package com.re.paas.internal.runtime.spi.locators;

import com.re.paas.api.clustering.AbstractNodeRoleDelegate;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.BaseSPILocator;
import com.re.paas.api.runtime.spi.SpiDelegate;
import com.re.paas.api.runtime.spi.SpiType;

public class NodeRoleSPILocator extends BaseSPILocator {

	public NodeRoleSPILocator() {
		addTypeSuffix("NodeRole");
	}
	
	@Override
	public SpiType spiType() {
		return SpiType.NODE_ROLE;
	}

	@Override
	public Class<? extends AbstractResource> classType() {
		return NodeRole.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractNodeRoleDelegate.class;
	}
	
}
