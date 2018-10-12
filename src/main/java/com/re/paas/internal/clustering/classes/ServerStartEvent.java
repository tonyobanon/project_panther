package com.re.paas.internal.clustering.classes;

import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.events.BaseEvent;

public class ServerStartEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;
	private final Server server;
	
	public ServerStartEvent(Server server) {
		super();
		this.server = server;
	}

	public Server getServer() {
		return server;
	}
	
	@Override
	public String name() {
		return "ServerStartEvent";
	}
}
