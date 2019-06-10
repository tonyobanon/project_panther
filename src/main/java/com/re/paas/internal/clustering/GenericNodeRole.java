package com.re.paas.internal.clustering;

import java.util.ArrayList;
import java.util.List;

import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.events.AbstractEventDelegate;

/**
 * 
 * This is a sample generic role.
 * 
 * @author Tony
 */
public class GenericNodeRole extends NodeRole {

	@Override
	public String name() {
		return "generic-role";
	}

	@Override
	public List<Class<? extends NodeRole>> dependencies() {
		return new ArrayList<>();
	}

	@Override
	public void start() {
		// Notify NodeRole Delegate
		AbstractEventDelegate.getInstance().dispatch(new RoleStartCompleteEvent(true), false);
	}

	@Override
	public void stop() {

	}

}
