package com.re.paas.internal.networking;

import java.net.InetAddress;

import com.re.paas.api.networking.InetAddressResolver;

import io.netty.util.NetUtil;

// In kubernetes, we always bind to eth0. For more information about Kubernetes networking model, see here:

// https://itnext.io/an-illustrated-guide-to-kubernetes-networking-part-1-d1ede3322727
// https://www.youtube.com/watch?v=WwQ62OyCNz4

public class InetAddressResolverImpl implements InetAddressResolver {

	public InetAddress getInetAddress() {
		return NetUtil.LOCALHOST;
	}
}
