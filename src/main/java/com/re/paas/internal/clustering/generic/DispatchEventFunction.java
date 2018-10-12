package com.re.paas.internal.clustering.generic;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.generic.GenericFunction;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.internal.clustering.GenericNodeRole;

public class DispatchEventFunction extends AbstractClusterFunction<BaseEvent, Object> {

	@Override
	public Class<? extends NodeRole> role() {
		return GenericNodeRole.class;
	}

	@Override
	public Function id() {
		return GenericFunction.DISPATCH_EVENT;
	}

	@Override
	public Object delegate(BaseEvent t) {
		AbstractEventDelegate.getInstance().dispatch(t, false);
		return null;
	}

}
