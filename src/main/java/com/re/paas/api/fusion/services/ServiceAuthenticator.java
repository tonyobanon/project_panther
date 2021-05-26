package com.re.paas.api.fusion.services;

import com.re.paas.api.fusion.RoutingContext;

public interface ServiceAuthenticator {

	boolean authenticate(RoutingContext ctx);

}
