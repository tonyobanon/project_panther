package com.re.paas.api.fusion;

import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

public class Cookie {

  private final io.netty.handler.codec.http.cookie.Cookie nettyCookie;
  private boolean changed;
  private boolean fromUserAgent;

  public Cookie(String name, String value) {
    this.nettyCookie = new DefaultCookie(name, value);
    this.changed = true;
  }

  public Cookie(io.netty.handler.codec.http.cookie.Cookie nettyCookie) {
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
    return ServerCookieEncoder.STRICT.encode(nettyCookie);
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
