package com.re.paas.api.fusion.server;

public class DefaultServiceAuthenticator implements ServiceAuthenticator{

	@Override
	public boolean authenticate(RoutingContext ctx) {
		return true;
	}

}
