package com.re.paas.internal.runtime;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;
import com.re.paas.api.runtime.ComputeQuotaExceededEvent;

public class ExecutorFactoryEventListener implements EventListener {

	@Subscribe
	@BlockerTodo("We should not determine that a new node needs to be provisioned by merely looking at the thread count.")
	public void onComputeQuotaExceeded(ComputeQuotaExceededEvent evt) {

		// This is triggered, when this pool can no longer be upgraded
		// We contact the master, requesting for a new node to be provisioned

		// MasterNodeRole.getDelegate().getMasterRole().
	}
}
