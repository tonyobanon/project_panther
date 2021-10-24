package com.re.paas.api.fusion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public interface HttpServerRequest extends Cloneable, Serializable {

	Cookie[] getCookies();
	
	Enumeration<String> getHeaders(String name);
	
	String getHeader(String name);
	
	Enumeration<String> getHeaderNames();
	
	HttpMethod getMethod();
	
	String getPathInfo();
	
	String getPathTranslated();
	
	String getContextPath();
	
	String getQueryString();
	
	String getRequestURI();

	String getBodyAsString();

	JsonObject getBodyAsJson();

	JsonArray getBodyAsJsonArray();

	InputStream getInputStream();
	
	String getParameter(String name);
	
	Enumeration<String> getParameterNames();
	
	String[] getParameterValues(String name);
	
	Map<String, String[]> getParameterMap();
	
	String getProtocol();
	
	String getScheme();
	
	BufferedReader getReader();
	
	String getRemoteAddr();
	
	String getRemoteHost();

	Locale getLocale();
	
	Enumeration<Locale> getLocales();
	
	boolean isSecure();
	
	int getRemotePort();

	Collection<Part> getParts();
	
	Part getPart(String name);
	
}