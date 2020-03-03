package com.re.paas.api.fusion.services;


import com.re.paas.api.fusion.Route;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractServiceDelegate extends SpiDelegate<BaseService> {

	/**
	 * Is set to true, the master pod will distribute traffic to other worker pods in the cluster,
	 * rather than service the request itself. 
	 */
	protected static final Boolean distributeTrafficOnMaster = Boolean.parseBoolean(System.getenv("distributeTrafficOnMaster"));
	
	public static final String BASE_PATH = "/api";
	
	/**
	 * This returns the functionality that is associated with the specified route
	 * 
	 * @param route
	 * @return
	 */
	public abstract Functionality getFunctionality(Route route);

	/**
	 * This returns the first route that is associated with the specified functionality
	 * 
	 * @param functionality
	 * @return
	 */
	public abstract Route getService(Functionality functionality);
	
	
	public abstract void handler(RoutingContext ctx);

}
