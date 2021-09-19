package com.re.paas.api.clustering.protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.Factory;
import com.re.paas.api.runtime.SecureMethod;

public interface Server {

	static Server get(InetSocketAddress host) {
		return Factory.get(Server.class, new Object[] {host.getAddress(), host.getPort()});
	}
	
	@SecureMethod
	CompletableFuture<Void> start();
	
	Boolean isOpen();
	
	@SecureMethod
	CompletableFuture<Void> stop();
	
	InetAddress host();
	 
	Integer port();
}
