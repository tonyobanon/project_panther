package com.re.paas.api.fusion.services;

import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractServiceDelegate extends SpiDelegate<BaseService> {

	/**
	 * Is set to true, the master pod will distribute traffic to other worker pods in the cluster,
	 * rather than service the request itself. 
	 */
	protected static final Boolean distributeTrafficOnMaster = Boolean.parseBoolean(System.getenv("distributeTrafficOnMaster"));
	
	public abstract void handler(RoutingContext ctx);

}
