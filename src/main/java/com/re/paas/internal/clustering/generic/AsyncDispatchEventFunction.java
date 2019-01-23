package com.re.paas.internal.clustering.generic;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.generic.GenericFunction;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.events.BaseEvent;
import com.re.paas.internal.clustering.GenericNodeRole;

public class AsyncDispatchEventFunction extends AbstractClusterFunction<BaseEvent, Object> {

	@Override
	public Class<? extends NodeRole> role() {
		return GenericNodeRole.class;
	}

	@Override
	public Function id() {
		return GenericFunction.ASYNC_DISPATCH_EVENT;
	}

	/**
	 * Note: that since the event is dispatched asynchronously, subscriber code is
	 * executed on a separate thread, and our eager response of "true" does not
	 * necessarily mean that the dispatch was completely successful
	 */
	@Override
	public Object delegate(BaseEvent t) {
		AbstractEventDelegate.getInstance().dispatch(t, true);
		return Boolean.TRUE;
	}

}
