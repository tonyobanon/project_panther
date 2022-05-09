package com.re.paas.internal.fusion;

import com.re.paas.api.fusion.Cookie;

public class CookieImpl implements Cookie {

	private final String name;
	private String value;
	
	private String domain;
	private String path;
	private long maxAge;
	private boolean secure;
	private boolean httpOnly;

	public CookieImpl(String name, String value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Cookie setValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Cookie setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	@Override
	public String getDomain() {
		return domain;
	}

	@Override
	public Cookie setPath(String path) {
		this.path = path;
		return this;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public Cookie setMaxAge(long maxAge) {
		this.maxAge = maxAge;
		return this;
	}

	@Override
	public long getMaxAge() {
		return maxAge;
	}

	@Override
	public Cookie setSecure(boolean secure) {
		this.secure = secure;
		return this;
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public Cookie setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
		return this;
	}

	@Override
	public boolean isHttpOnyOnly() {
		return httpOnly;
	}

	
	

}
