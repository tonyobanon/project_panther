package com.re.paas.api.clustering.classes;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.clustering.NodeRegistry;

public class ClusteringUtils {
	
	public static Short getNodeId(String hostAddress) {
		NodeRegistry registry = NodeRegistry.get();
		
		for(BaseNodeSpec spec : registry.getNodes().values()) {
			if(spec.getRemoteAddress().getHostAddress().equals(hostAddress)) {
				return spec.getId();
			}
		}
		return null;
	}
	
	/**
	 * This returns the InetSocket Address for the well known address
	 * @return
	 */
	public static InetSocketAddress getInetSocketAddress() {
		NodeRegistry registry = NodeRegistry.get();
		return InetSocketAddress.createUnresolved(registry.getWkaHost().getHostAddress(), registry.getWkaInboundPort());
	}
	
	public static InetSocketAddress getInetSocketAddress(BaseNodeSpec spec) {
		return InetSocketAddress.createUnresolved(spec.getRemoteAddress().getHostAddress(), spec.getInboundPort());
	}

	private static Map<Short, InetSocketAddress> getAllNodeAddresses() {
		
		NodeRegistry registry = NodeRegistry.get();
		Map<Short, BaseNodeSpec> clusterNodes = registry.getNodes();

		Map<Short, InetSocketAddress> result = new HashMap<Short, InetSocketAddress>(clusterNodes.size());
		
		for (BaseNodeSpec v : clusterNodes.values()) {
			result.put(v.getId(), getInetSocketAddress(v));
		}	
		
		return result;
	}
	
	public static Collection<InetSocketAddress> generateAddressList(ClusterDestination destination) {

		NodeRegistry registry = NodeRegistry.get();
		Map<Short, BaseNodeSpec> clusterNodes = registry.getNodes();

		Collection<InetSocketAddress> result = null;

		switch (destination) {

		case ALL_NODES:

			result = getAllNodeAddresses().values();

		case OTHER_NODES:

			Map<Short, InetSocketAddress> addresses = getAllNodeAddresses();
			addresses.remove(registry.getNodeId());
			
			result = addresses.values();

			break;

		case SPECIFIC_NODE:

			InetSocketAddress addr = null;

			if (destination.getDestination() instanceof InetSocketAddress) {
				addr = (InetSocketAddress) destination.getDestination();
			} else {

				Short nodeId = (Short) destination.getDestination();
				BaseNodeSpec spec = clusterNodes.get(nodeId);

				addr = getInetSocketAddress(spec);
			}

			result = new ArrayList<>(1);
			result.add(addr);

			break;
		}

		return result;
	}
}
