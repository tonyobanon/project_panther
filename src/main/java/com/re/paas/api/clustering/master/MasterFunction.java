package com.re.paas.api.clustering.master;

import com.re.paas.api.clustering.Function;

public enum MasterFunction implements Function {

	MEMBER_SETUP(0, false);

	private short id;
	private boolean isAsync;
	
	private MasterFunction(int id, boolean isAsync) {
		this.id = (short) id;
		this.isAsync = isAsync;
	}
	
	public static MasterFunction from(int value) {

		switch (value) {
		
		case 0:
			return MEMBER_SETUP;
	
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
