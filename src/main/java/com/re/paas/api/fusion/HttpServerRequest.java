package com.re.paas.api.fusion;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface HttpServerRequest extends Cloneable {

	Map<String, Cookie> cookies();

	Buffer body();
	
	Collection<FileUpload> fileUploads();

	HttpMethod method();

	String rawMethod();

	boolean isSSL();

	String scheme();

	String path();

	String query();

	String host();
	
	List<Locale> locales();

	MultiMap headers();

	String getHeader(String headerName);

	MultiMap params();

	String getParam(String paramName);

	InetSocketAddress remoteAddress();

	InetSocketAddress localAddress();

	MultiMap formAttributes();

	CharSequence getFormAttribute(String attributeName);
}