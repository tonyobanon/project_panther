package com.re.paas.api.clustering;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.classes.ClusterDestination;
import com.re.paas.api.clustering.classes.ClusteringUtils;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.Resource;
import com.re.paas.api.runtime.spi.SpiType;

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

	public static <P> Map<String, Object> execute(Function function, P parameter) {
		return execute(function, parameter, Object.class);
	}

	public static <R, P> Map<String, R> execute(Function function, P parameter, Class<R> responseType) {
		return execute(ClusterDestination.SPECIFIC_NODE.setDestination(ClusteringUtils.getInetSocketAddress()),
				function, parameter, responseType);
	}

	public static <P> Map<String, Object> execute(ClusterDestination destination, Function function, P parameter) {
		return execute(destination, function, parameter, Object.class);
	}

	public static <R, P> Map<String, R> execute(ClusterDestination destination, Function function, P parameter,
			Class<R> responseType) {

		Collection<InetSocketAddress> addresses = ClusteringUtils.generateAddressList(destination);
		Map<String, R> result = new HashMap<>(addresses.size());

		addresses.forEach(addr -> {
			R r = Client.get(addr.getAddress(), addr.getPort()).execute(function, parameter, responseType).join();
			result.put(addr.getAddress().getHostAddress(), r);
		});

		return result;
	}

	public static <R, P> R execute(BaseNodeSpec destination, Function function, P parameter, Class<R> responseType) {
		return execute(ClusterDestination.spec(destination), function, parameter, responseType)
				.get(destination.getRemoteAddress().getHostAddress());
	}
}
