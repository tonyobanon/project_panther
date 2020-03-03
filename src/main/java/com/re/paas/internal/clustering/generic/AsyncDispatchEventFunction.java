package com.re.paas.internal.clustering.generic;

import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.generic.GenericFunction;

public class AsyncDispatchEventFunction extends DispatchEventFunction {

	@Override
	public Function id() {
		return GenericFunction.ASYNC_DISPATCH_EVENT;
	}

}
