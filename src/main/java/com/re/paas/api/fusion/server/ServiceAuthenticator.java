package com.re.paas.api.fusion.server;

/**
 * Services that do not intend to use the built-in functionality based auth
 * mechanism, can use a custom service authenticator. All you need to do is to
 * implement this interface, and pass in your class on {@code EndpointMethod}.
 * <br>
 * Note: If you are using custom authenticators, then FusionHelper.getUserId(...)
 * will always return null;
 **/
public interface ServiceAuthenticator {

	boolean authenticate(RoutingContext ctx);

}
