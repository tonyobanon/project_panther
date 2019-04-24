package com.re.paas.internal.infra.database;

public abstract class ThrottlePolicy {
	
	public abstract QueryThrottlePolicy forQuery();

}
