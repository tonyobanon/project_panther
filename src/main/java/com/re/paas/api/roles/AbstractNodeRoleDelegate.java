package com.re.paas.api.roles;

import java.util.Map;

import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractNodeRoleDelegate extends SpiDelegate<AbstractRole> {
	
	public abstract Map<String, AbstractRole> getNodeRoles();

	public abstract AbstractMasterRole getMasterRole();
	
	public boolean isMaster() {
		return getMasterRole() != null;
	}
}
