package com.re.paas.api.fusion.services;

import com.re.paas.api.fusion.RoutingContext;

public class DefaultServiceAuthenticator implements ServiceAuthenticator{

	@Override
	public boolean authenticate(RoutingContext ctx) {
		return true;
	}

}
