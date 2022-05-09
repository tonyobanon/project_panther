package com.re.paas.api.fusion;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

public interface HttpServerResponse extends Cloneable, Serializable {
	
	void addCookie(Cookie cookie);
	
	int getStatus();

	HttpServerResponse setStatus(int statusCode);
	
	Collection<String> getHeaderNames();
	
	Collection<String> getHeaders(String name);
	
	HttpServerResponse setHeader(String name, String value);
	
	HttpServerResponse addHeader(String name, String value);

	HttpServerResponse writeHtml(String contents);
	
	HttpServerResponse write(String contentType, byte[] contents);
	
	HttpServerResponse render(BaseComponent component, Boolean testMode);
	
	HttpServerResponse render(BaseComponent component);

	boolean isCommited();

	Locale getLocale();
	
	HttpServerResponse setLocale(Locale locale);
	
	void reset();

	void flushBuffer();
	
	void resetBuffer();
	
	void setBufferSize(int size);
	
	int getBufferSize();
	
	OutputStream getOutputStream();
	
	String getContentType();
	
	HttpServerResponse setContentType(String type);
	
	HttpServerResponse setContentLength(int len);
	
	HttpServerResponse setContentLengthLong(long len);

	PrintWriter getWriter();
	
}