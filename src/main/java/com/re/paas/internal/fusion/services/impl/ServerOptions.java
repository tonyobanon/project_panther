package com.re.paas.internal.fusion.services.impl;

import java.net.InetAddress;

public class ServerOptions {

	private boolean enableCors;
	
	private InetAddress host;
	private Integer port;
	private Integer sslPort;

	public ServerOptions() {
	}

	/*
	 * public MicroServiceOptions withBaseURI(String baseURI) { if
	 * (!MicroserviceContainer.uriPattern.matcher(baseURI).matches()) { throw
	 * new RuntimeException(
	 * "Incorrect pattern for base URI, example of a correct pattern is: /base or /base1/base2"
	 * ); } this.baseURI = baseURI; return this; }
	 */


	public InetAddress getHost() {
		return host;
	}

	public ServerOptions withHost(InetAddress host) {
		this.host = host;
		return this;
	}
	
	public Integer getPort() {
		return port;
	}

	public ServerOptions withPort(Integer port) {
		this.port = port;
		return this;
	}

	public Integer getSslPort() {
		return sslPort;
	}

	public ServerOptions withSslPort(Integer sslPort) {
		this.sslPort = sslPort;
		return this;
	}

	public ServerOptions enableCors() {
		this.enableCors = true;
		return this;
	}

	public ServerOptions disableCors() {
		this.enableCors = false;
		return this;
	}

	public boolean isCorsEnabled() {
		return this.enableCors;
	}

}
