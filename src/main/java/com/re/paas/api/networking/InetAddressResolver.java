package com.re.paas.api.networking;

import java.net.InetAddress;

import com.re.paas.api.designpatterns.Singleton;

public interface InetAddressResolver {
	
	public static InetAddressResolver get() {
		return Singleton.get(InetAddressResolver.class);
	}
	
	InetAddress getInetAddress();
	
}
