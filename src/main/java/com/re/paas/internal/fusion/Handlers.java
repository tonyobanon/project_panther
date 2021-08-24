package com.re.paas.internal.fusion;

import java.util.function.Consumer;

import com.re.paas.api.fusion.RoutingContext;

public class Handlers {

	static Consumer<RoutingContext> defaultTailHandler(String appId) {
		return (ctx -> {
			// Todo: Add any initialization task(s) here
		});
	}

	static Consumer<RoutingContext> defaultHeadHandler(String appId) {
		return (ctx -> {
			// Todo: Add any finalization task(s) here
		});
	}
}
