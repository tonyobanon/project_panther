package com.re.paas.api.classes;

public class NumberBound {
	
	private final Integer min;
	private final Integer max;
	
	public NumberBound(Integer min, Integer max) {
		this.min = min;
		this.max = max;
	}

	public Integer getMin() {
		return min;
	}

	public Integer getMax() {
		return max;
	}
	
}
