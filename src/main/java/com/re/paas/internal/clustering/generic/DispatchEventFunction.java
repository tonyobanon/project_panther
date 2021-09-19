package com.re.paas.internal.clustering.generic;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.generic.GenericFunction;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.roles.AbstractRole;

public class DispatchEventFunction extends AbstractClusterFunction<BaseEvent, Object> {

	@Override
	public Class<? extends AbstractRole> role() {
		return AbstractRole.class;
	}

	@Override
	public Function id() {
		return GenericFunction.DISPATCH_EVENT;
	}
	
	protected boolean isAsync() {
		return false;
	}

	@Override
	public Object delegate(BaseEvent t) {
		try {
			AbstractEventDelegate.getInstance().dispatch(t, isAsync());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
