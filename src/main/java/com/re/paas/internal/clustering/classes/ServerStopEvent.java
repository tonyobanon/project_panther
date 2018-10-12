package com.re.paas.internal.clustering.classes;

import com.re.paas.api.events.BaseEvent;

public class ServerStopEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String name() {
		return "ServerStopEvent";
	}
	
}
