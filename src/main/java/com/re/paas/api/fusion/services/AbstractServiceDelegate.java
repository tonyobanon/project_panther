package com.re.paas.api.fusion.services;

import java.util.List;

import com.google.common.collect.Multimap;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.Route;
import com.re.paas.api.fusion.server.ServiceDescriptor;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractServiceDelegate extends SpiDelegate<BaseService> {

	public static final String BASE_PATH = "/api";

	public  abstract Multimap<Route, ServiceDescriptor> getServiceDescriptors();
	
	public abstract ServiceDescriptor getServiceDescriptor(Route route);
	
	public abstract Functionality getServiceFunctionality(Route route);

	public abstract List<String> getFunctionalityService(Functionality functionality);

}
