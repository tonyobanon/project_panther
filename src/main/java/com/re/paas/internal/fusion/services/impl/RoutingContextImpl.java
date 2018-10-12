package com.re.paas.internal.fusion.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.re.paas.api.fusion.server.Buffer;
import com.re.paas.api.fusion.server.Cookie;
import com.re.paas.api.fusion.server.FileUpload;
import com.re.paas.api.fusion.server.HttpServerRequest;
import com.re.paas.api.fusion.server.HttpServerResponse;
import com.re.paas.api.fusion.server.HttpStatusCodes;
import com.re.paas.api.fusion.server.JsonArray;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.MultiMap;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.server.Session;

public class RoutingContextImpl implements RoutingContext {

	private final HttpServerRequest request;
	private final HttpServerResponse response;
	
	private final Session<String, String> session;

	RoutingContextImpl(HttpServletRequest req, HttpServletResponse resp) {

		this.request = new HttpServerRequestImpl(req);
		this.response = new HttpServerResponseImpl(resp);
		
		this.session = new SessionImpl(this);
	}

	@Override
	public @Nullable Cookie getCookie(String name) {
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
	public @Nullable List<String> queryParam(String query) {
		return request.params().getAll(query);
	}

	@Override
	public Session<String, String> session() {
		return session;
	}
}
