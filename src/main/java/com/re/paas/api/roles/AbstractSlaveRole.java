package com.re.paas.api.roles;

import com.re.paas.api.clustering.ClusteringServices;

public abstract class AbstractSlaveRole extends AbstractRole {

	@Override
	public String name() {
		return "cluster-slave";
	}

	@Override
	public boolean applies() {
		return !ClusteringServices.get().isMaster();
	}

}
