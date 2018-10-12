package com.re.paas.api.classes;

public class LongWrapper extends ObjectWrapper<Long> {

	public LongWrapper() {
		this(0L);
	}
	
	public LongWrapper(Long value) {
		super(value);
	}
	
	public LongWrapper add() {
		return add(1);
	}
	
	public LongWrapper add(long value) {
		this.value += value;
		return this;
	}
	
	public LongWrapper minus() {
		return minus(1);
	}
	
	public LongWrapper minus(long value) {
		this.value -= value;
		return this;
	}
}
