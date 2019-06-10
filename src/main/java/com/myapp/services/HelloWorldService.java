package com.myapp.services;

import com.myapp.functionalities.HelloWorldFunctionalities;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.FusionEndpoint;
import com.re.paas.api.fusion.server.RoutingContext;

public class HelloWorldService extends BaseService {

	@Override
	public String uri() {
		return "/hello-world";
	}

	@FusionEndpoint(functionality = HelloWorldFunctionalities.Constants.SAY_HELLO, uri = "/say-hello")
	public static void sayHello(RoutingContext ctx) {
		ctx.response().putHeader("Content-Type", "application/json").write("Hello World").end();
	}
}
