package com.re.paas.api.fusion.server;

import java.util.function.Consumer;

public class RouteHandler {

	private Consumer<RoutingContext> handler;
	private boolean isBlocking;
	
	public RouteHandler(Consumer<RoutingContext> handler, boolean isBlocking) {
		this.handler = handler;
		this.isBlocking = isBlocking;
	}

	public Consumer<RoutingContext> getHandler() {
		return handler;
	}

	public RouteHandler setHandler(Consumer<RoutingContext> handler) {
		this.handler = handler;
		return this;
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public RouteHandler setBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
		return this;
	}
}
