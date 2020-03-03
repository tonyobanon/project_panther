package com.re.paas.api.fusion;

import com.re.paas.api.classes.Exceptions;

public class Route {

	private String uri;
	private HttpMethod method;

	public Route() {
	}

	public Route(String uri, HttpMethod method) {
		this.uri = uri;
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public Route setUri(String uri) {
		this.uri = uri;
		return this;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public Route setMethod(HttpMethod method) {
		this.method = method;
		return this;
	}

	public static Route fromString(String value) {

		try {
			if (value.equals("*")) {
				return new Route();
			}
			if (value.startsWith("/")) {
				if (value.contains("-")) {
					// uri with method
					String[] arr = value.split("-");
					return new Route(arr[0], HttpMethod.valueOf(arr[1]));
				} else {
					// only uri
					return new Route().setUri(value);
				}
			} else {
				// only method
				return new Route().setMethod(HttpMethod.valueOf(value));
			}
		} catch (Exception e) {
			Exceptions.throwRuntime("Unable to parse route string: " + value);
			return null;
		}
	}

	@Override
	public String toString() {
		return
		// Match all paths and methods
		this.getUri() == null && this.getMethod() == null ? "*" :
		// Match by method only
				this.getUri() == null && this.getMethod() != null ? this.getMethod().name() :
				// Match by path only
						this.getUri() != null && this.getMethod() == null ? this.getUri() :
						// Match by method and path
								this.getUri() != null && this.getMethod() != null
										? this.getUri() + "-" + this.getMethod().name()
										: "";
	}
}
