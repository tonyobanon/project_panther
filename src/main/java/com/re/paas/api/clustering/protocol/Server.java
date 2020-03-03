package com.re.paas.api.clustering.protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.designpatterns.Factory;

public interface Server {

	public static Server get(InetSocketAddress host) {
		return Factory.get(Server.class, new Object[] {host.getAddress(), host.getPort()});
	}
	
	CompletableFuture<Void> start();
	
	Boolean isOpen();
	
	CompletableFuture<Void> stop();
	
	InetAddress host();
	 
	Integer port();
}
