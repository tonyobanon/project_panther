package com.re.paas.internal.fusion.services;

import com.re.paas.api.fusion.Endpoint;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.components.platform.global_navigation.GlobalNavigation;
import com.re.paas.api.fusion.components.platform.global_navigation.Tab;
//import com.re.paas.api.fusion.components.platform.global_navigation.GlobalNavigation;
//import com.re.paas.api.fusion.components.platform.global_navigation.Tab;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.tasks.Affinity;

public class HelloService extends BaseService {

	@Override
	public String uri() {
		return "/hello";
	}

	@Endpoint(uri = "/greet", affinity = Affinity.ANY)
	public static void greet(RoutingContext ctx) {
		ctx.response()
		.render(
			new GlobalNavigation().addTab(new Tab()),
			true
		);
	}

}
