package com.re.paas.api.fusion;

public interface Cookie {

	String getValue();

	Cookie setValue(String value);

	String getName();

	Cookie setDomain(String domain);

	String getDomain();

	Cookie setPath(String path);

	String getPath();

	Cookie setMaxAge(long maxAge);

	long getMaxAge();

	Cookie setSecure(boolean secure);

	boolean isSecure();

	Cookie setHttpOnly(boolean httpOnly);

	boolean isHttpOnyOnly();

}