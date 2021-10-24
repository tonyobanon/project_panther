package com.re.paas.internal.fusion;

import com.re.paas.api.fusion.HttpServerRequest;
import com.re.paas.api.fusion.HttpServerResponse;
import com.re.paas.api.fusion.HttpStatusCodes;
import com.re.paas.api.fusion.Route;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.Session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class RoutingContextImpl implements RoutingContext {

	private static final long serialVersionUID = 1L;
	
	private final Route route;
	
	private HttpServerRequest request;
	private HttpServerResponse response;

	private final Session<String, Object> session;

	RoutingContextImpl(Route route, HttpServletRequest req, HttpServletResponse resp, Boolean addSession) {

		this.route = route;
		
		this.request = new HttpServerRequestImpl(req);
		this.response = new HttpServerResponseImpl(req, resp);

		this.session = addSession ? new SessionImpl(this) : null;
	}
	
	@Override
	public Route route() {
		return route;
	}

	@Override
	public HttpServerResponse response() {
		return response;
	}

	@Override
	public HttpServerRequest request() {
		return request;
	}

	@Override
	public void reroute(String path) {
		this.response.setStatus(HttpStatusCodes.SC_FOUND).setHeader("Location", path).flushBuffer();
	}

	@Override
	public Session<String, Object> session() {
		return session;
	}
	
	void setRequest(HttpServerRequest request) {
		this.request = request;
	}

	void setResponse(HttpServerResponse response) {
		this.response = response;
	}
}
