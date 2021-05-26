package com.re.paas.api.clustering;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.Singleton;
import com.re.paas.api.clustering.classes.ClusterDestination;
import com.re.paas.api.clustering.classes.ClusteringUtils;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.Collections;

public interface Function extends Resource {

	public static AbstractFunctionDelegate getDelegate() {
		return Singleton.get(AbstractFunctionDelegate.class);
	}

	public static short getId(Function function) {
		return getDelegate().getId(function);
	}

	public static Function fromId(Short id) {
		return getDelegate().getFunction(id);
	}

	public String namespace();

	public short contextId();

	public boolean isAsync();

	@Override
	default SpiType getSpiType() {
		return SpiType.FUNCTION;
	}

	public static <P> Map<String, CompletableFuture<Object>> execute(Function function, P parameter) {
		return execute(function, parameter, Object.class);
	}

	public static <R, P> Map<String, CompletableFuture<R>> execute(Function function, P parameter,
			Class<R> responseType) {
		return execute(ClusterDestination.SPECIFIC_NODE.setDestination(ClusteringServices.get().getMaster().getHost()),
				function, parameter, responseType);
	}

	public static <P> Map<String, CompletableFuture<Object>> execute(ClusterDestination destination, Function function,
			P parameter) {
		return execute(destination, function, parameter, Object.class);
	}

	public static <R, P> Map<String, CompletableFuture<R>> execute(ClusterDestination destination, Function function,
			P parameter, Class<R> responseType) {

		Collection<InetSocketAddress> addresses = ClusteringUtils.generateAddressList(destination);
		Map<String, CompletableFuture<R>> result = new HashMap<>(addresses.size());

		addresses.forEach(addr -> {
			CompletableFuture<R> r = Client.get(addr).execute(function, parameter, responseType);
			result.put(addr.getAddress().getHostAddress(), r);
		});

		return result;
	}
	
	public static <P> void executeWait(ClusterDestination destination, Function function,
			P parameter) {
		
		Map<String, CompletableFuture<Object>> r = execute(destination, function, parameter);
		
		for (CompletableFuture<Object> f : r.values()) {
			f.join();
		}
	}

	public static <R, P> CompletableFuture<R> execute(Short memberId, Function function, P parameter,
			Class<R> responseType) {

		Map<String, CompletableFuture<R>> r = execute(ClusterDestination.SPECIFIC_NODE.setDestination(memberId),
				function, parameter, responseType);
		return Collections.firstValue(r);
	}

}
