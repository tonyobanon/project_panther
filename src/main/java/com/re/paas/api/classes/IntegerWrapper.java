package com.re.paas.api.classes;

public class IntegerWrapper extends ObjectWrapper<Integer>{

	public IntegerWrapper() {
		this(0);
	}
	
	public IntegerWrapper(Integer value) {
		super(value);
	}
	
	public IntegerWrapper add() {
		return add(1);
	}
	
	public IntegerWrapper add(Integer value) {
		
		if(value >= 0) {
			this.value += value;
		} else {
			this.value -= Math.abs(value);
		}
		
		return this;
	}
	
	public IntegerWrapper minus() {
		return minus(1);
	}
	
	public IntegerWrapper minus(Integer value) {
		this.value -= value;
		return this;
	}
}
