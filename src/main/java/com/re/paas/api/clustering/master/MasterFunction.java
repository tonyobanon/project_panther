package com.re.paas.api.clustering.master;

import com.re.paas.api.clustering.Function;

public enum MasterFunction implements Function {

	CLUSTER_JOIN(0, false),
	CLUSTER_LEAVE(1, false),
	
	START_PROVISIONED_NODE(2, true),
	RELEASE_PROVISIONED_NODE(3, true);
	
	
	private short id;
	private boolean isAsync;
	
	private MasterFunction(int id, boolean isAsync) {
		this.id = (short) id;
		this.isAsync = isAsync;
	}
	
	public static MasterFunction from(int value) {

		switch (value) {
		
		case 0:
			return CLUSTER_JOIN;
			
		case 1:
			return CLUSTER_LEAVE;
			
		case 2:
			return START_PROVISIONED_NODE;
			
		case 3:
			return RELEASE_PROVISIONED_NODE;
	
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	@Override
	public String namespace() {
		return "master";
	}
	
	@Override
	public short contextId() {
		return id;
	}
	
	@Override
	public boolean isAsync() {
		return isAsync;
	}
	
}
