package com.re.paas.internal.clustering;

import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.clustering.classes.BaseNodeSpec;
import com.re.paas.api.clustering.events.NodeJoinEvent;
import com.re.paas.api.clustering.events.NodeLeaveEvent;
import com.re.paas.api.clustering.events.NodeStateChangeEvent;
import com.re.paas.api.clustering.protocol.ClientFactory;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;
import com.re.paas.api.logging.Logger;

/**
 * 
 * This is the main event listener that intercepts events and dispatches it to
 * all roles for processing
 * 
 * @author Tony
 */
public class NodeEventListener implements EventListener {

	@Subscribe
	public void onNodeJoin(NodeJoinEvent evt) {

		NodeRegistry registry = NodeRegistry.get();
		ClientFactory cFactory = ClientFactory.get();

		for (BaseNodeSpec spec : evt.getNodes()) {

			registry.getNodes().put(spec.getId(), spec);

			Logger.get().debug("NodeJoinEvent -> event id: " + evt.getEventId() + " id: " + spec.getId() + " address: "
					+ spec.getRemoteAddress());

			if (!registry.getNodeId().equals(spec.getId())) {
				cFactory.addNode(spec.getId());
			}
		}

		NodeRole.get().values().forEach(r -> {
			r.onNodeJoin(evt);
		});
	}

	@Subscribe
	public void onNodeLeave(NodeLeaveEvent evt) {

		NodeRegistry registry = NodeRegistry.get();
		registry.getNodes().remove(evt.getNodeId());

		if (!registry.getNodeId().equals(evt.getNodeId())) {
			ClientFactory cFactory = ClientFactory.get();
			cFactory.releaseNode(evt.getNodeId());
		}

		NodeRole.get().values().forEach(r -> {
			r.onNodeLeave(evt);
		});
	}

	@Subscribe
	public void onNodeStateChange(NodeStateChangeEvent evt) {

		NodeRegistry.get().getNodes().get(evt.getNodeId()).setState(evt.getNewState());

		Logger.get().debug("NodeStateChangeEvent -> event id: " + evt.getEventId() + " id: " + evt.getNodeId()
				+ " new state: " + evt.getNewState().name());

		NodeRole.get().values().forEach(r -> {
			r.onNodeStateChange(evt);
		});
	}

}
