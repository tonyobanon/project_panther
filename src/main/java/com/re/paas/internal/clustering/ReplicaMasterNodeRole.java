package com.re.paas.internal.clustering;

import java.util.List;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.infra.cloud.CloudEnvironment;

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
	@BlockerTodo("Also ingest the file that indicates whether the Platform is installed")
	public void start() {
		
		
		
	}

}
