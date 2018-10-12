package com.re.paas.internal.classes.spec;

public class TokenCredentials {

	private String url;
	private String token;

	public String getToken() {
		return token;
	}

	public TokenCredentials setToken(String token) {
		this.token = token;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public TokenCredentials setUrl(String url) {
		this.url = url;
		return this;
	}
}
 