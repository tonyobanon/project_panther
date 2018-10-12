package com.re.paas.internal.networking;

import java.net.Inet6Address;
import java.net.InetAddress;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.networking.AddressResolver;
import com.re.paas.internal.errors.ApplicationError;

public class AddressResolverImpl implements AddressResolver {

	private static InetAddress HTTP_HOST;
	private static InetAddress CLUSTERING_HOST;
	
	@Override
	public InetAddress httpHost() {
		
		if(HTTP_HOST != null) {
			return HTTP_HOST;
		}

		// Use a public network address (if available)
		for (InetAddress address : NIC.getAllAddress()) {
			if (NIC.isInetAddressPublic(address)) {
				return address;
			}
		}

		// Use a private network address instead
		for (InetAddress address : NIC.getAllAddress()) {
			if (NIC.isInetAddressPrivate(address)) {
				return address;
			}
		}

		HTTP_HOST = (InetAddress) Exceptions
				.throwRuntime(PlatformException.get(ApplicationError.COULD_NOT_FIND_SUITABLE_HTTP_ADDRESS));
		return HTTP_HOST;
	}

	@Override
	public InetAddress clusteringHost() {
		
		if(CLUSTERING_HOST != null) {
			return CLUSTERING_HOST;
		}

		// Use a private network address
		for (InetAddress address : NIC.getAllAddress()) {
			if (NIC.isInetAddressPrivate(address)) {

				if (address instanceof Inet6Address) {
					return (InetAddress) Exceptions
							.throwRuntime(new RuntimeException("IPv6 addresses not supported for clustering"));
				}

				return address;
			}
		}

		CLUSTERING_HOST = (InetAddress) Exceptions
				.throwRuntime(PlatformException.get(ApplicationError.COULD_NOT_FIND_SUITABLE_CLUSTERING_ADDRESS));
		return CLUSTERING_HOST;
	}

}
