package com.re.paas.api.clustering;

import java.util.Map;

import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractNodeRoleDelegate extends SpiDelegate<NodeRole> {

	public abstract Map<String, NodeRole> getAllRoles();
	
	public abstract Map<String, NodeRole> getNodeRoles();

	public abstract AbstractMasterNodeRole getMasterRole();
	
	public boolean isMaster() {
		return getMasterRole() != null;
	}
}
