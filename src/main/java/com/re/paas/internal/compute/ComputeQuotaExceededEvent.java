package com.re.paas.internal.compute;

import com.re.paas.api.events.BaseEvent;

public class ComputeQuotaExceededEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	@Override
	public String name() {
		return "ComputeQuotaExceededEvent";
	}

}
