package com.re.paas.api.clustering.events;

import com.re.paas.api.events.BaseEvent;

public class MemberLeaveEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private final Short memberId;
	
	public MemberLeaveEvent(Short memberId) {
		this.memberId = memberId;
	}

	public Short getMemberId() {
		return memberId;
	}
}
