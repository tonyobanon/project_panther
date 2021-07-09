package com.re.paas.internal.fusion;

import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.ServerCookieEncoder;

import com.re.paas.api.fusion.Cookie;

public class CookieImpl implements Cookie {

	private final io.netty.handler.codec.http.Cookie nettyCookie;
	private boolean changed;
	private boolean fromUserAgent;

	public CookieImpl(String name, String value) {
		this.nettyCookie = new DefaultCookie(name, value);
		this.changed = true;
	}

	public CookieImpl(io.netty.handler.codec.http.Cookie nettyCookie) {
		this.nettyCookie = nettyCookie;
		fromUserAgent = true;
	}

	public String getValue() {
		return nettyCookie.value();
	}

	public Cookie setValue(final String value) {
		nettyCookie.setValue(value);
		this.changed = true;
		return this;
	}

	public String getName() {
		return nettyCookie.name();
	}

	public Cookie setDomain(final String domain) {
		nettyCookie.setDomain(domain);
		this.changed = true;
		return this;
	}

	public String getDomain() {
		return nettyCookie.domain();
	}

	public Cookie setPath(final String path) {
		nettyCookie.setPath(path);
		this.changed = true;
		return this;
	}

	public String getPath() {
		return nettyCookie.path();
	}

	public Cookie setMaxAge(final long maxAge) {
		nettyCookie.setMaxAge(maxAge);
		this.changed = true;
		return this;
	}

	public long getMaxAge() {
		return nettyCookie.maxAge();
	}

	public Cookie setSecure(final boolean secure) {
		nettyCookie.setSecure(secure);
		this.changed = true;
		return this;
	}

	public boolean isSecure() {
		return nettyCookie.isSecure();
	}

	public Cookie setHttpOnly(final boolean httpOnly) {
		nettyCookie.setHttpOnly(httpOnly);
		this.changed = true;
		return this;
	}

	public boolean isHttpOnyOnly() {
		return nettyCookie.isHttpOnly();
	}

	public String encode() {
		return ServerCookieEncoder.encode(nettyCookie);
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isFromUserAgent() {
		return fromUserAgent;
	}

}
