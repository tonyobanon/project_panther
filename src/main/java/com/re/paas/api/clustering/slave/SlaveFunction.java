package com.re.paas.api.clustering.slave;

import com.re.paas.api.clustering.Function;

public enum SlaveFunction implements Function {
	;
	
	private short id;
	private boolean isAsync;
	
	private SlaveFunction(int id, boolean isAsync) {
		this.id = (short) id;
		this.isAsync = isAsync;
	}
	
	public static SlaveFunction from(int value) {

		switch (value) {
		
	
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	@Override
	public String namespace() {
		return "slave";
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
