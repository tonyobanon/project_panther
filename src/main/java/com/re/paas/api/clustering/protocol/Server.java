package com.re.paas.api.clustering.protocol;

import java.net.InetAddress;

import com.re.paas.api.designpatterns.Factory;

public interface Server {

	public static Server get(InetAddress host, Integer port) {
		return Factory.get(Server.class, new Object[] {host, port});
	}
	
	void start();
	
	Boolean isOpen();
	
	void stop();
	
	InetAddress host();
	 
	Integer port();
}
