package com.re.paas.api.fusion.server;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public interface RoutingContext {

	Cookie getCookie(String name);

	RoutingContext addCookie(Cookie cookie);

	String getBodyAsString();

	String getBodyAsString(String encoding);

	JsonObject getBodyAsJson();

	JsonArray getBodyAsJsonArray();

	Buffer getBody();

	Collection<FileUpload> fileUploads();

	HttpServerResponse response();

	HttpServerRequest request();

	void reroute(String path);

	List<Locale> acceptableLocales();

	MultiMap queryParams();

	List<String> queryParam(String query);

	Session<String, String> session();
}