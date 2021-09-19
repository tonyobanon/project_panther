package com.re.paas.api.clustering.protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.clustering.Function;

public interface Client {

	public static Client get(InetSocketAddress address) {
		return ClientFactory.get().getClient(address);
	}

	public static Client get(Short memberId) {
		return ClientFactory.get().getClient(memberId);
	}

	<P, R> CompletableFuture<R> execute(Function function, P parameter, Class<R> R);

	default <P> CompletableFuture<Object> execute(Function function, P parameter) {
		return execute(function, parameter, Object.class);
	}

	InetAddress host();

	Integer port();

	void close();

	Short getMemberId();

	Short getClientId();

}
