package com.re.paas.internal.clustering.generic;

import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.generic.GenericFunction;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.ParameterizedExecutable;

public class AsyncExecuteInvokableFunction extends ExecuteInvokableFunction {

	@Override
	public Class<? extends AbstractRole> role() {
		return AbstractRole.class;
	}

	@Override
	public Function id() {
		return GenericFunction.ASYNC_EXECUTE_INVOKABLE;
	}
	
	@Override
	public Object delegate(ParameterizedExecutable<Object, ?> function) {
		ExecutorFactory.get().executeLocal(function).join();
		return null;
	}
}
