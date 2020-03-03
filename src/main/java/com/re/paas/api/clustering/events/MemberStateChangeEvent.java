package com.re.paas.api.clustering.events;

import com.re.paas.api.clustering.classes.MemberStatus;
import com.re.paas.api.events.BaseEvent;

public class MemberStateChangeEvent extends BaseEvent {

	private static final long serialVersionUID = 1L;

	private Short memberId;
	private MemberStatus newState;

	public Short getMemberId() {
		return memberId;
	}

	public MemberStateChangeEvent setMemberId(Short memberId) {
		this.memberId = memberId;
		return this;
	}

	public MemberStatus getNewState() {
		return newState;
	}

	public MemberStateChangeEvent setNewState(MemberStatus newState) {
		this.newState = newState;
		return this;
	}
}
