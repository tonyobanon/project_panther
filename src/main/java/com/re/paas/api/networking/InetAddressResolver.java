package com.re.paas.api.networking;

import java.net.InetAddress;

import com.re.paas.api.Singleton;

public interface InetAddressResolver {
	
	public static InetAddressResolver get() {
		return Singleton.get(InetAddressResolver.class);
	}
	
	InetAddress getInetAddress();
	
}
