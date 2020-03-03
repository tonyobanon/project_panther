package com.re.paas.api.clustering.protocol;

import java.net.InetSocketAddress;

import com.re.paas.api.designpatterns.Singleton;

public interface ClientFactory {

	public static ClientFactory get() {
		return Singleton.get(ClientFactory.class);
	}
	
	Client getClient(Short memberId);
	
	Client getClient(InetSocketAddress host);

	void addMember(Short memberId);

	void releaseMember(Short memberId);
	
	boolean isRotated();
	
	Short maxRotatedClients();

}
