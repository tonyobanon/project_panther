package com.re.paas.api.fusion;

import java.io.Serializable;

public interface RoutingContext extends Serializable {

	Route route();

	HttpServerResponse response();

	HttpServerRequest request();

	void reroute(String path);

	Session<String, Object> session();
}
