package com.re.paas.internal.tasks;

import com.re.paas.api.clustering.events.MemberLeaveEvent;
import com.re.paas.api.events.EventListener;
import com.re.paas.api.events.Subscribe;

/**
 * Shutdown {@link TaskDelegate#taskExecutor} when the member is leaving
 * 
 * @author Tony
 */
public class ClusterMembersListener implements EventListener {

	@Subscribe
	public void onMemberLeave(MemberLeaveEvent evt) {
		TaskDelegate.taskExecutor.shutdown();
	}
}
