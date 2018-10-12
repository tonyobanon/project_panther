package com.re.paas.api.classes;

public class NumberBound {
	
	private Integer min;
	private Integer max;
	
	public Integer getMin() {
		return min;
	}
	
	public NumberBound setMin(Integer min) {
		this.min = min;
		return this;
	}
	
	public Integer getMax() {
		return max;
	}
	
	public NumberBound setMax(Integer max) {
		this.max = max;
		return this;
	}	
}
