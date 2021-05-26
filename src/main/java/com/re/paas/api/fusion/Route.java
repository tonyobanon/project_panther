package com.re.paas.api.fusion;

import com.re.paas.api.classes.Exceptions;

public final class Route {
	
	private final String appId;
	
	private String uri;
	private HttpMethod method;
	

	public Route(String appId) {
		this.appId = appId;
	}

	public Route(String appId, String uri, HttpMethod method) {
		
		this(appId);
		
		this.setUri(uri);
		this.setMethod(method);
	}

	public String getUri() {
		return uri;
	}

	public Route setUri(String uri) {
		
		if (uri.isEmpty()) {
			uri = null;
		} else {
			assert uri.startsWith("/");
		}
		
		this.uri = uri;
		return this;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public Route setMethod(HttpMethod method) {
		
		if (method == HttpMethod.ALL) {
			method = null;
		}
		
		this.method = method;
		return this;
	}
	

	public String getAppId() {
		return appId;
	}


	public static Route fromString(String value) {

		String[] parts = value.split("#");
		
		String appId = parts[0];
		
		value = parts[1];
		
		try {
			if (value.equals("*")) {
				return new Route(appId);
			}
			if (value.startsWith("/")) {
				if (value.contains("-")) {
					// uri with method
					String[] arr = value.split("-");
					return new Route(appId, arr[0], HttpMethod.valueOf(arr[1]));
				} else {
					// only uri
					return new Route(appId).setUri(value);
				}
			} else {
				// only method
				return new Route(appId).setMethod(HttpMethod.valueOf(value));
			}
		} catch (Exception e) {
			Exceptions.throwRuntime("Unable to parse route string: " + value);
			return null;
		}
	}

	@Override
	public String toString() {
		String value =
		// Match all paths and methods
		this.getUri() == null && this.getMethod() == null ? "*" :
		// Match by method only
				this.getUri() == null && this.getMethod() != null ? this.getMethod().name() :
				// Match by path only
						this.getUri() != null && this.getMethod() == null ? this.getUri() :
						// Match by method and path
								this.getUri() != null && this.getMethod() != null
										? this.getUri() + "-" + this.getMethod().name()
										: null;
		
		assert value != null;
		
		return this.getAppId() + "#" + value;
	}
}
