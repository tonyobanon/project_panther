package com.re.paas.internal.fusion;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.fusion.HttpServerRequest;
import com.re.paas.api.fusion.HttpServerResponse;
import com.re.paas.api.fusion.StaticFileContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class StaticFileContextImpl implements StaticFileContext {
	
	private final String appId;
	private final String staticPath;
	
	private HttpServerRequest request;
	private HttpServerResponse response;
	
	public static final Map<String, byte[]> fileCache = new HashMap<>();
	
	public StaticFileContextImpl(String appId, String staticPath, HttpServletRequest req, HttpServletResponse resp) {
		this.appId = appId;
		this.staticPath = staticPath;
		
		this.request = new HttpServerRequestImpl(req);
		this.response = new HttpServerResponseImpl(req, resp);
	}

	@Override
	public String appId() {
		return appId;
	}

	@Override
	public String staticPath() {
		return staticPath;
	}
	
	@Override
	public HttpServerRequest request() {
		return request;
	}

	@Override
	public HttpServerResponse response() {
		return response;
	}

	void setRequest(HttpServerRequest request) {
		this.request = request;
	}

	void setResponse(HttpServerResponse response) {
		this.response = response;
	}
}
