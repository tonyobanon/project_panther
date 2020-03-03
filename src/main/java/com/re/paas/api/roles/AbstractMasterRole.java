package com.re.paas.api.roles;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.ClusteringServices;

/**
 * All {@link AbstractClusterFunction Cluster Functions} that binds only to the
 * cluster master should use this class in its
 * {@link AbstractClusterFunction#role() role()} method. Note that if a subclass
 * of {@link AbstractMasterRole} is used instead, then there may be other
 * conditions to meet.
 * 
 * 
 * @author anthony.anyanwu
 *
 */
public abstract class AbstractMasterRole extends AbstractRole {

	@Override
	public String name() {
		return "cluster-master";
	}

	@Override
	public boolean applies() {
		return ClusteringServices.get().isMaster();
	}
}
