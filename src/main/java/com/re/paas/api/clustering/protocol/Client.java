package com.re.paas.api.clustering.protocol;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.NodeRegistry;

public interface Client {

	public static Client get() {
		NodeRegistry registry = NodeRegistry.get();
		return ClientFactory.get().getClient(registry.getWkaHost(), registry.getWkaInboundPort());
	}

	public static Client get(InetAddress host, Integer port) {
		return ClientFactory.get().getClient(host, port);
	}

	public static Client get(Short nodeId) {
		return ClientFactory.get().getClient(nodeId);
	}

	<P, R> CompletableFuture<R> execute(Function function, P parameter, Class<R> R, Integer pThreshold);

	default <P> CompletableFuture<Object> execute(Function function, P parameter, Integer pThreshold) {
		return execute(function, parameter, Object.class, pThreshold);
	}

	default <P> CompletableFuture<Object> execute(Function function, P parameter) {
		return execute(function, parameter, Object.class, -1);
	}

	default <P, R> CompletableFuture<R> execute(Function function, P parameter, Class<R> R) {
		return execute(function, parameter, R, -1);
	}

	InetAddress host();

	Integer port();

	void close();

	Short getNodeId();

	Short getClientId();

}
