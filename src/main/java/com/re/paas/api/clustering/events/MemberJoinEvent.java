package com.re.paas.api.clustering.events;

import com.re.paas.api.events.BaseEvent;

/**
 * Note: When this event is triggered, the member in question may not have had
 * all it's applicable roles registered.
 * 
 * @author anthonyanyanwu
 *
 */
public class MemberJoinEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private final Short memberId;

	public MemberJoinEvent(Short memberId) {
		this.memberId = memberId;
	}

	public Short getMemberId() {
		return memberId;
	}
}
