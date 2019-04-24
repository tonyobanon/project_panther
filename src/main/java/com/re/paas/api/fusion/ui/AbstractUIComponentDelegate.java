package com.re.paas.api.fusion.ui;

import java.util.List;

import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.runtime.spi.SpiDelegate;

public abstract class AbstractUIComponentDelegate extends SpiDelegate<AbstractComponent> { 

	public static final String DEFAULT_CONSOLE_URI = "";
	
	public abstract void handler(RoutingContext ctx);

	public abstract List<String> getUriParams(String uri);

	public abstract String getUri(Functionality f);
}
