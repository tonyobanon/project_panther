package com.re.paas.api.fusion;

import java.util.Map;

public interface HttpServerResponse extends Cloneable {

	Map<String, Cookie> cookies();
	
	void addCookie(Cookie cookie);
	
	int getStatusCode();

	HttpServerResponse setStatusCode(int statusCode);

	MultiMap headers();
	
	HttpServerResponse putHeader(String name, String value);

	HttpServerResponse write(Buffer chunk);

	HttpServerResponse write(String chunk, String enc);

	HttpServerResponse write(String chunk);
	
	HttpServerResponse render(BaseComponent component, Boolean testMode);
	
	HttpServerResponse render(BaseComponent component);

	void end();

	boolean ended();
	
	int bytesWritten();

}