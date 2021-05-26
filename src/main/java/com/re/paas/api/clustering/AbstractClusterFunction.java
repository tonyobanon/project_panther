package com.re.paas.api.clustering;

import com.re.paas.api.Singleton;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public abstract class AbstractClusterFunction<P, R> extends AbstractResource {

	public AbstractClusterFunction() {
		super(SpiType.CLUSTER_FUNCTION);
	}
	
	public abstract Class<? extends AbstractRole> role();

	public abstract Function id();

	public static AbstractClusterFunctionDelegate getDelegate() {
		return Singleton.get(AbstractClusterFunctionDelegate.class);
	}

	public abstract R delegate(P t);

	public static AbstractClusterFunction<Object, Object> get(Function function) {
		return getDelegate().getClusterFunction(function);
	}
	
}
