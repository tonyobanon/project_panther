package com.re.paas.internal.fusion;

import java.util.function.Consumer;

import com.re.paas.api.classes.ThreadContext;
import com.re.paas.api.fusion.RoutingContext;

public class Handlers {

	static Consumer<RoutingContext> defaultTailHandler() {
		return (ctx -> {
			// Remove thread local data
			ThreadContext.clear();
		});
	}

	static Consumer<RoutingContext> defaultHeadHandler() {
		return (ctx -> {

			// Note: container pools request threads, we need to create new LocalThread
			// context
			ThreadContext.newRequestContext();
		});
	}

}
