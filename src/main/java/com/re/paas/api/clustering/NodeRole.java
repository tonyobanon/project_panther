package com.re.paas.api.clustering;

import java.util.List;
import java.util.Map;

import com.re.paas.api.clustering.events.NodeJoinEvent;
import com.re.paas.api.clustering.events.NodeLeaveEvent;
import com.re.paas.api.clustering.events.NodeStateChangeEvent;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.infra.cloud.Tags;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public abstract class NodeRole extends AbstractResource {
	
	public NodeRole() {
		super(SpiType.NODE_ROLE);
	}
	
	public String toString() {
		return name();
	}
	
	public static Map<String, NodeRole> get() {
		return getDelegate().getNodeRoles();
	}

	public static AbstractNodeRoleDelegate getDelegate() {
		return Singleton.get(AbstractNodeRoleDelegate.class);
	}

	public abstract String name();

	public abstract List<Class<? extends NodeRole>> dependencies();

	public void start() {
	}

	public void stop() {
	}
	
	protected Boolean hasMasterTrait() {
		String val = CloudEnvironment.get().getInstanceTags().get(Tags.MASTER_TAG);
		return Boolean.getBoolean(val);
	}
	
	public boolean applies() {
		return true;
	}

	public void onNodeJoin(NodeJoinEvent evt) {
	}

	public void onNodeLeave(NodeLeaveEvent evt) {
	}

	public void onNodeStateChange(NodeStateChangeEvent evt) {

	}

}
