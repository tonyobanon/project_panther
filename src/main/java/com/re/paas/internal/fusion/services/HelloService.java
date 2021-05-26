package com.re.paas.internal.fusion.services;

import com.re.paas.api.fusion.Endpoint;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.components.platform.icon.Icon;
import com.re.paas.api.fusion.components.platform.icon.Name;
import com.re.paas.api.fusion.components.platform.icon.Type;
import com.re.paas.api.fusion.services.BaseService;

public class HelloService extends BaseService {
	
	@Override
	public String uri() {
		return "/hello";
	}

	@Endpoint(uri = "/greet")
	public static void getTypes(RoutingContext ctx) {
		
		 ctx.response().render(new Icon()
				 .setName(Name.ACCOUNT)
				 .setType(Type.ACTION));
	}

}
