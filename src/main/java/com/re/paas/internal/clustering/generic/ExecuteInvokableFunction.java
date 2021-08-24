package com.re.paas.internal.clustering.generic;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.generic.GenericFunction;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ParameterizedExecutable;

public class ExecuteInvokableFunction extends AbstractClusterFunction<ParameterizedExecutable<Object, ?>, Object> {

	@Override
	public Class<? extends AbstractRole> role() {
		return AbstractRole.class;
	}

	@Override
	public Function id() {
		return GenericFunction.EXECUTE_INVOKABLE;
	}
	
	@Override
	public Object delegate(ParameterizedExecutable<Object, ?> function) {	
		Object r = ExecutorFactory.get().executeLocal(function).join();
		return r;
	}
}
