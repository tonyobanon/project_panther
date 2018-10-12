package com.re.paas.api.fusion.server;

public class CookieUtil {

	public static Cookie toFusionCookie(javax.servlet.http.Cookie c) {

		Cookie cookie = new Cookie(c.getName(), c.getValue());

		if (c.getDomain() != null) {
			cookie.setDomain(c.getDomain());
		}
		cookie.setMaxAge(c.getMaxAge()).setSecure(c.getSecure()).setHttpOnly(c.isHttpOnly());

		if (c.getPath() != null) {
			cookie.setPath(c.getPath());
		}

		return cookie;
	}

	public static javax.servlet.http.Cookie toServletCookie(Cookie c) {

		javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(c.getName(), c.getValue());

		if (c.getDomain() != null) {
			cookie.setDomain(c.getDomain());
		}

		cookie.setMaxAge((int) c.getMaxAge());
		cookie.setSecure(c.isSecure());
		cookie.setHttpOnly(c.isHttpOnyOnly());

		if (c.getPath() != null) {
			cookie.setPath(c.getPath());
		}

		return cookie;
	}
}
