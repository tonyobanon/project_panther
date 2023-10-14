package com.re.paas.internal.fusion.services;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.re.paas.api.fusion.Endpoint;
import com.re.paas.api.fusion.RoutingContext;
//import com.re.paas.api.fusion.components.platform.global_navigation.GlobalNavigation;
//import com.re.paas.api.fusion.components.platform.global_navigation.Tab;
//import com.re.paas.api.fusion.components.platform.icon.Icon;
//import com.re.paas.api.fusion.components.platform.icon.Type;
//import com.re.paas.api.fusion.components.platform.illustration.Illustration;
//import com.re.paas.api.fusion.components.platform.illustration.Name;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.tasks.Affinity;

public class HelloService extends BaseService {
	
	List<FunctionalInterface> l = new ArrayList<>();

	@Override
	public String uri() {		
		return "/hello";
	}

	@Endpoint(uri = "/greet", affinity = Affinity.ANY)
	public static void greet(RoutingContext ctx) {
		
//		ctx.response().render(new GlobalNavigation()
//				
//				.setAppName("My Application")
//				.setIcon(new Icon()
//						.setName(com.re.paas.api.fusion.components.platform.icon.Name.CONNECTED_APPS)
//						.setType(Type.STANDARD)
//				)
//				
//				.addTab(new Tab().setAssistiveText("My First Tab").setCloseable(true).setIdentifier("my_first_tab")
//						.setTitle("Tab 1").setContent(
//								new Illustration().setName(Name.MAINTENANCE).setVerticallyAlign(true)
//						))
//				
//				.addTab(new Tab().setAssistiveText("My Second Tab").setCloseable(true).setIdentifier("my_second_tab")
//						.setTitle("Tab 2").setContent(
//								new Illustration().setName(Name.LAKE_MOUNTAIN).setVerticallyAlign(true)
//						)));
	}

}
