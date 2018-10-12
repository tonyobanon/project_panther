package com.re.paas.internal.clustering;

import java.util.List;

import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.clustering.NodeRole;

public class ReplicaMasterNodeRole extends NodeRole {

	@Override
	public String name() {
		return "cluster-replica-master";
	}

	@Override
	public List<Class<? extends NodeRole>> dependencies() {
		return new FluentArrayList<Class<? extends NodeRole>>();
	}

	@Override
	public boolean applies() {
		CloudEnvironment env = CloudEnvironment.get();
		return (!env.clusteringHost().equals(env.wkaHost())) && hasMasterTrait();
	}
	
	@Override
	public void start() {
		
		
		
	}

}
