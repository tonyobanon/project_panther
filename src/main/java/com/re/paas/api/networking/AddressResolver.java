package com.re.paas.api.networking;

import java.net.InetAddress;

import com.re.paas.api.designpatterns.Singleton;

public interface AddressResolver {
	
	public static AddressResolver get() {
		return Singleton.get(AddressResolver.class);
	}
	
	InetAddress httpHost();
	
	InetAddress clusteringHost();
	
}
