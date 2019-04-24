package com.re.paas.api.fusion.services;

import java.util.List;

import com.google.common.collect.Multimap;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.Route;
import com.re.paas.api.fusion.server.RouteHandler;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractServiceDelegate extends SpiDelegate<BaseService> {

	public static final String BASE_PATH = "/api";

	public  abstract Multimap<Route, RouteHandler> getRouteHandlers();
	
	public abstract List<RouteHandler> getRouteHandlers(Route route);
	
	public abstract Functionality getRouteFunctionality(Route route);

	public abstract List<String> getFunctionalityRoute(Functionality functionality);

}
