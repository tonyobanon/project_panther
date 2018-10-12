package com.re.paas.api.clustering.protocol;

import java.net.InetAddress;

import com.re.paas.api.designpatterns.Singleton;

public interface ClientFactory {

	public static ClientFactory get() {
		return Singleton.get(ClientFactory.class);
	}
	
	Client getClient(Short nodeId);
	
	Client getClient(InetAddress host, Integer port);

	void addNode(Short nodeId);

	void releaseNode(Short nodeId);
	
	boolean isRotated();
	
	Short maxRotatedClients();
	
}
