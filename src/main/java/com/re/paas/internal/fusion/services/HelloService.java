package com.re.paas.internal.fusion.services;

import com.re.paas.api.fusion.Endpoint;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.services.BaseService;

public class HelloService extends BaseService {

	@Override
	public String uri() {
		return "/hello";
	}

	@Endpoint(uri = "/greet")
	public static void getTypes(RoutingContext ctx) {

//		ctx.response()
//		.render(
//			new GlobalNavigation().addTab(new Tab()),
//			true
//		);
	}

}
