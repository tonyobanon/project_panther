package com.re.paas.internal.fusion;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.re.paas.api.fusion.Buffer;
import com.re.paas.api.fusion.Cookie;
import com.re.paas.api.fusion.FileUpload;
import com.re.paas.api.fusion.HttpServerRequest;
import com.re.paas.api.fusion.HttpServerResponse;
import com.re.paas.api.fusion.HttpStatusCodes;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.fusion.MultiMap;
import com.re.paas.api.fusion.Route;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.Session;
import com.re.paas.api.runtime.SecureMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RoutingContextImpl implements RoutingContext {

	private static final long serialVersionUID = 1L;
	
	private final Route route;
	
	private final HttpServerRequest request;
	private HttpServerResponse response;

	private final Session<String, Object> session;

	RoutingContextImpl(Route route, HttpServletRequest req, HttpServletResponse resp, Boolean addSession) {

		this.route = route;
		
		this.request = new HttpServerRequestImpl(req);
		this.response = new HttpServerResponseImpl(resp);

		this.session = addSession ? new SessionImpl(this) : null;
	}
	
	@Override
	public Route route() {
		return route;
	}

	@Override
	public Cookie getCookie(String name) {
		return request.cookies().get(name);
	}

	@Override
	public RoutingContext addCookie(Cookie cookie) {
		response.addCookie(cookie);
		return this;
	}

	@Override
	public String getBodyAsString() {
		return getBody() != null ? getBody().toString() : null;
	}

	@Override
	public String getBodyAsString(String encoding) {
		return getBody() != null ? getBody().toString(encoding) : null;
	}

	@Override
	public JsonObject getBodyAsJson() {
		return getBody() != null ? new JsonObject(getBody().toString()) : null;
	}

	@Override
	public JsonArray getBodyAsJsonArray() {
		return getBody() != null ? new JsonArray(getBody().toString()) : null;
	}

	@Override
	public Buffer getBody() {
		return request.body();
	}

	@Override
	public Collection<FileUpload> fileUploads() {
		return request.fileUploads();
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
		this.response.setStatusCode(HttpStatusCodes.SC_FOUND).putHeader("Location", path).end();
	}

	@Override
	public List<Locale> acceptableLocales() {
		return request.locales();
	}

	@Override
	public MultiMap queryParams() {
		return request.params();
	}

	@Override
	public List<String> queryParam(String query) {
		return request.params().getAll(query);
	}

	@Override
	public Session<String, Object> session() {
		return session;
	}

	@SecureMethod
	public RoutingContextImpl setResponse(HttpServerResponse response) {
		this.response = response;
		return this;
	}
}
