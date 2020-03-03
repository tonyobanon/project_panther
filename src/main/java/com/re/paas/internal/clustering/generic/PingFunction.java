package com.re.paas.internal.clustering.generic;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.generic.GenericFunction;
import com.re.paas.api.roles.AbstractRole;

public class PingFunction extends AbstractClusterFunction<String, String> {

	@Override
	public Class<? extends AbstractRole> role() {
		return AbstractRole.class;
	}

	@Override
	public Function id() {
		return GenericFunction.PING;
	}

	@Override
	public String delegate(String t) {
		return "Hello " + t;
	}

}
