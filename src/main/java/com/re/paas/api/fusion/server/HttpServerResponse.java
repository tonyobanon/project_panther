package com.re.paas.api.fusion.server;

import java.util.Map;

public interface HttpServerResponse {

	Map<String, Cookie> cookies();
	
	void addCookie(Cookie cookie);
	
	int getStatusCode();

	HttpServerResponse setStatusCode(int statusCode);

	MultiMap headers();
	
	HttpServerResponse putHeader(String name, String value);

	HttpServerResponse write(Buffer chunk);

	HttpServerResponse write(String chunk, String enc);

	HttpServerResponse write(String chunk);

	void end(String chunk);

	void end(String chunk, String enc);

	void end(Buffer chunk);

	void end();

	boolean ended();
	
	int bytesWritten();

}