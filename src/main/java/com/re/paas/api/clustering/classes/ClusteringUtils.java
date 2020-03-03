package com.re.paas.api.clustering.classes;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.clustering.Member;

public class ClusteringUtils {

	@BlockerTodo("Don't do this. Inside maintain a separate Map for host to memberId mapping")
	public static Short getMemberId(String hostAddress) {

		for (Member member : ClusteringServices.get().getMembers().values()) {
			if (member.getHost().getHostString().equals(hostAddress)) {
				return member.getMemberId();
			}
		}
		return null;
	}

	public static Collection<InetSocketAddress> generateAddressList(ClusterDestination destination) {

		Collection<InetSocketAddress> result = null;

		switch (destination) {

		case ALL_NODES:

			result = ClusteringServices.get().getMembers().entrySet().stream().map(e -> e.getValue().getHost())
					.collect(Collectors.toUnmodifiableList());

		case OTHER_NODES:

			result = ClusteringServices.get().getMembers().entrySet().stream()
					.filter(e -> !e.getKey().equals(ClusteringServices.get().getMember().getMemberId()))
					.map(e -> e.getValue().getHost()).collect(Collectors.toUnmodifiableList());

			break;

		case SPECIFIC_NODE:

			InetSocketAddress addr = null;

			if (destination.getDestination() instanceof InetSocketAddress) {
				addr = (InetSocketAddress) destination.getDestination();
			} else {

				Short memberId = (Short) destination.getDestination();
				addr = ClusteringServices.get().getMember(memberId).getHost();
			}

			result = new ArrayList<>(1);
			result.add(addr);

			break;
		}

		return result;
	}
}
