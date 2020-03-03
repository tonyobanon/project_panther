package com.re.paas.internal.networking;

import java.net.InetAddress;
import java.net.NetworkInterface;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.networking.InetAddressResolver;

// We always bind to eth0. For more information about Kubernetes networking model, see here:

// https://itnext.io/an-illustrated-guide-to-kubernetes-networking-part-1-d1ede3322727
// https://www.youtube.com/watch?v=WwQ62OyCNz4

public class InetAddressResolverImpl implements InetAddressResolver {

	public InetAddress getInetAddress() {
		
		try {
			
			NetworkInterface nic = NetworkInterface.getByName("eth0");
			return nic.getInetAddresses().nextElement();
			
		} catch (Exception e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}
}
