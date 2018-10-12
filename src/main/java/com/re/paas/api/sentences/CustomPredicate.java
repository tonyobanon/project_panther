package com.re.paas.api.sentences;

import com.re.paas.api.classes.ClientAware;
import com.re.paas.api.classes.ClientRBRef;

/**
 * This is a very small subset of all predicates available. It depicts possible actions that can be done by {@code SubjectEntityKind}
 * */
public enum CustomPredicate {
	
	CREATED, ADDED, REGISTERED, DELETED, UPDATED, DOWNLOADED, SUBMITTED, APPROVED, DECLINED, VIEWED, ASSIGNED, UNASSIGNED;
	
	@Override
	@ClientAware
	public String toString() {
		return ClientRBRef.get(this.name().toLowerCase()).toString();
	}
}
