package com.re.paas.internal.clustering;

import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.Function;
import com.re.paas.internal.clustering.protocol.ChannelUtils;
import com.re.paas.internal.compute.Scheduler;

import io.netty.channel.Channel;

public class Functions {

	public static <P, R> R execute(Function function, P parameter) {
		return execute(function, parameter, null);
	}

	public static <P, R> R execute(Function function, P parameter, Channel channel) {

		@SuppressWarnings("unchecked")
		AbstractClusterFunction<P, R> clusterFunction = ((AbstractClusterFunction<P, R>) AbstractClusterFunction
				.get(function));

		R result = null;

		if (function.isAsync()) {
			Scheduler.now(() -> {
				clusterFunction.delegate(parameter);
			});
		} else {
			result = clusterFunction.delegate(parameter);
		}
		
		if (channel != null) {
			ChannelUtils.sendResponse(channel, result);
		}

		return result;
	}
}
