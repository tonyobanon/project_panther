package com.re.paas.api.fusion.services;

import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.StaticFileContext;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractServiceDelegate extends SpiDelegate<BaseService> {

	public abstract void handler(RoutingContext ctx);
	
	public abstract void handler(StaticFileContext ctx);

}
