package com.re.paas.internal.spi.locators;

import com.google.common.collect.Lists;
import com.re.paas.api.clustering.AbstractNodeRoleDelegate;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.spi.BaseSPILocator;
import com.re.paas.api.spi.SpiDelegate;
import com.re.paas.api.spi.SpiTypes;

public class NodeRoleSPILocator extends BaseSPILocator {

	@Override
	public SpiTypes spiType() {
		return SpiTypes.NODE_ROLE;
	}

	@Override
	public Iterable<String> classSuffix() {
		return Lists.newArrayList("NodeRole");
	}

	@Override
	public Class<?> classType() {
		return NodeRole.class;
	}
	
	@Override
	public Class<? extends SpiDelegate<?>> delegateType() {
		return AbstractNodeRoleDelegate.class;
	}
	
}
