package com.re.paas.internal.fusion;

import com.re.paas.api.fusion.Cookie;

public class CookieUtil {

	public static Cookie toFusionCookie(jakarta.servlet.http.Cookie c) {

		Cookie cookie = new CookieImpl(c.getName(), c.getValue());

		if (c.getDomain() != null) {
			cookie.setDomain(c.getDomain());
		}
		cookie.setMaxAge(c.getMaxAge()).setSecure(c.getSecure()).setHttpOnly(c.isHttpOnly());

		if (c.getPath() != null) {
			cookie.setPath(c.getPath());
		}

		return cookie;
	}

	public static jakarta.servlet.http.Cookie toServletCookie(Cookie c) {

		jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie(c.getName(), c.getValue());

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
