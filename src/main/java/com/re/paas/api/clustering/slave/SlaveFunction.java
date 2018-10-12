package com.re.paas.api.clustering.slave;

import com.re.paas.api.clustering.Function;

public enum SlaveFunction implements Function {
	
	XYZ;
	
	@Override
	public String namespace() {
		return "slave";
	}
	
	@Override
	public short contextId() {
		return 0;
	}
	
	public static SlaveFunction from(int value) {

		switch (value) {
		
		case 0:
			return XYZ;
	
		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}
	
	@Override
	public boolean isAsync() {
		return false;
	}
}
