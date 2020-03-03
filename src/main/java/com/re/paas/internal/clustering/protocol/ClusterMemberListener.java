package com.re.paas.internal.clustering.protocol;

import com.re.paas.api.clustering.events.MemberJoinEvent;
import com.re.paas.api.clustering.events.MemberLeaveEvent;
import com.re.paas.api.clustering.protocol.ClientFactory;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;

/**
 * 
 * This listens for when members join and leave the cluster, inorder to notify
 * ClientFactory. This is especially useful for knowing know when new nodes join
 * the cluster, after the current member has joined
 * 
 * 
 * @author Tony
 */
public class ClusterMemberListener implements EventListener {

	@Subscribe
	public void onMemberJoin(MemberJoinEvent evt) {
		ClientFactory.get().addMember(evt.getMemberId());
	}

	@Subscribe
	public void onMemberLeave(MemberLeaveEvent evt) {
		ClientFactory.get().releaseMember(evt.getMemberId());
	}

}
