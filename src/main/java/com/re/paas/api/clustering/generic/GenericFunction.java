package com.re.paas.api.clustering.generic;

import com.re.paas.api.clustering.Function;

public enum GenericFunction implements Function {

	PING(0), DISPATCH_EVENT(1), ASYNC_DISPATCH_EVENT(2, true),
	EXECUTE_INVOKABLE(3), ASYNC_EXECUTE_INVOKABLE(4, true)
	;

	private short id;
	private boolean isAsync;
	
	private GenericFunction(int id) {
		this(id, false);
	}

	private GenericFunction(int id, boolean isAsync) {
		this.id = (short) id;
		this.isAsync = isAsync;
	}

	public static GenericFunction from(int value) {

		switch (value) {
		
		case 4:
			return ASYNC_EXECUTE_INVOKABLE;
			
		case 3:
			return EXECUTE_INVOKABLE;

		case 2:
			return ASYNC_DISPATCH_EVENT;

		case 1:
			return DISPATCH_EVENT;

		case 0:
			return PING;

		default:
			throw new IllegalArgumentException("An invalid value was provided");
		}
	}

	@Override
	public String namespace() {
		return "generic";
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
