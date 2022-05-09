package com.re.paas.internal.fusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.fusion.Cookie;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.HttpServerRequest;
import com.re.paas.api.fusion.JsonArray;
import com.re.paas.api.fusion.JsonObject;
import com.re.paas.api.fusion.Part;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

public class HttpServerRequestImpl implements HttpServerRequest {

	private static final long serialVersionUID = 1L;

	private final HttpServletRequest req;

	HttpServerRequestImpl(HttpServletRequest req) {
		this.req = req;
	}

	@Override
	public Cookie[] getCookies() {
		int len = req.getCookies().length;
		Cookie[] cookies = new Cookie[len];
		
		for (int i = 0; i < len; i++) {
			cookies[i] = CookieHelper.toFusionCookie(req.getCookies()[i]);
		}
		
		return cookies;
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		return this.req.getHeaders(name);
	}

	@Override
	public String getHeader(String name) {
		return this.req.getHeader(name);
	}
	
	@Override
	public Enumeration<String> getHeaderNames() {
		return this.req.getHeaderNames();
	}

	@Override
	public HttpMethod getMethod() {
		return HttpMethod.valueOf(this.req.getMethod());
	}

	@Override
	public String getPathInfo() {
		return this.req.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return this.req.getPathTranslated();
	}

	@Override
	public String getContextPath() {
		return this.req.getContextPath();
	}

	@Override
	public String getQueryString() {
		return this.req.getQueryString();
	}

	@Override
	public String getRequestURI() {
		return this.req.getRequestURI();
	}
	
	@Override
	public String getBodyAsString() {
		StringWriter sw = new StringWriter();
		
		try {
			getReader().transferTo(sw);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		return sw.toString();
	}

	@Override
	public JsonObject getBodyAsJson() {
		return new JsonObject(getBodyAsString());
	}

	@Override
	public JsonArray getBodyAsJsonArray() {
		return new JsonArray(getBodyAsString());
	}
	
	@Override
	public InputStream getInputStream() {
		try {
			return this.req.getInputStream();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}
	
	@Override
	public String getParameter(String name) {
		return this.req.getParameter(name);
	}
	
	@Override
	public Enumeration<String> getParameterNames() {
		return this.req.getParameterNames();
	}
	
	@Override
	public String[] getParameterValues(String name) {
		return this.req.getParameterValues(name);
	}
	
	@Override
	public Map<String, String[]> getParameterMap() {
		return this.req.getParameterMap();
	}
	
	@Override
	public String getProtocol() {
		return this.req.getProtocol();
	}
	
	@Override
	public String getScheme() {
		return this.req.getScheme();
	}
	
	@Override
	public BufferedReader getReader() {
		try {
			return this.req.getReader();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}
	
	@Override
	public String getRemoteAddr() {
		return this.req.getRemoteAddr();
	}
	
	@Override
	public String getRemoteHost() {
		return this.req.getRemoteHost();
	}
	
	@Override
	public Locale getLocale() {
		return this.req.getLocale();
	}
	
	@Override
	public Enumeration<Locale> getLocales() {
		return this.req.getLocales();
	}
	
	@Override
	public boolean isSecure() {
		return this.req.isSecure();
	}
	
	@Override
	public int getRemotePort() {
		return this.req.getRemotePort();
	}

	@Override
	public Collection<Part> getParts() {
		Collection<jakarta.servlet.http.Part> jakartaParts = null;
		try {
			jakartaParts = this.req.getParts();
		} catch (ServletException | IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		Collection<Part> parts = new ArrayList<Part>(jakartaParts.size());
		
		jakartaParts.forEach(p -> {
			parts.add(new PartImpl(p));
		});
		
		return parts;
	}

	@Override
	public Part getPart(String name) {
		jakarta.servlet.http.Part jakartaPart = null;
		try {
			jakartaPart = this.req.getPart(name);
		} catch (ServletException | IOException e) {
			Exceptions.throwRuntime(e);
		}
		
		return new PartImpl(jakartaPart);
	}
	
	@Override
	public String getSocketSessionId() {
		return CookieHelper.getCookie(this.req.getCookies(), "socketSessionId");
	}

}
