package com.re.paas.internal.roles;

import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.clustering.events.MemberJoinEvent;
import com.re.paas.api.clustering.events.MemberLeaveEvent;
import com.re.paas.api.clustering.events.MemberStateChangeEvent;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;
import com.re.paas.api.roles.AbstractRole;

/**
 * 
 * When roles are initially started, the current list of cluster members can be retrieved from
 * {@link ClusteringServices}. However, inorder for these roles to be aware of future changes to 
 * cluster membership, this class listens for member events, and notifies each role
 * 
 * @author Tony
 */
public class ClusterMembersListener implements EventListener {

	@Subscribe
	public void onMemberJoin(MemberJoinEvent evt) {

		AbstractRole.get().values().forEach(r -> {
			r.onMemberJoin(evt);
		});
	}

	@Subscribe
	public void onMemberLeave(MemberLeaveEvent evt) {

		AbstractRole.get().values().forEach(r -> {
			r.onMemberLeave(evt);
		});
	}

	@Subscribe
	public void onMemberStateChange(MemberStateChangeEvent evt) {

		AbstractRole.get().values().forEach(r -> {
			r.onMemberStateChange(evt);
		});
	}
}
