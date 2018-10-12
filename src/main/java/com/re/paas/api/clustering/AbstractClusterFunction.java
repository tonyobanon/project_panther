package com.re.paas.api.clustering;

import com.re.paas.api.designpatterns.Singleton;

public abstract class AbstractClusterFunction<P, R> {

	public abstract Class<? extends NodeRole> role();

	public abstract Function id();

	public static AbstractClusterFunctionDelegate getDelegate() {
		return Singleton.get(AbstractClusterFunctionDelegate.class);
	}

	public abstract R delegate(P t);

	public static AbstractClusterFunction<Object, Object> get(Function function) {
		return getDelegate().getClusterFunction(function);
	}
	
}
