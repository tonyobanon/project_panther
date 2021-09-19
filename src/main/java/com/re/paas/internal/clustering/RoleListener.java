package com.re.paas.internal.clustering;

import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;
import com.re.paas.internal.roles.RoleInitEvent;

public class RoleListener implements EventListener {

	/**
	 * When a role is initialized, register it on the current member
	 * 
	 * @param evt
	 */
	@Subscribe
	public void onRoleInit(RoleInitEvent evt) {
		ClusteringServices.get().addRole(evt.getRoleName());
	}
}
