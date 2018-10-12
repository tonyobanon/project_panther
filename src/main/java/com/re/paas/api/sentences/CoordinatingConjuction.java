package com.re.paas.api.sentences;

import com.re.paas.api.classes.ClientAware;
import com.re.paas.api.classes.ClientRBRef;

public enum CoordinatingConjuction {
	
	FOR, AND, NOR, BUT, OR, YET, SO;
	
	@Override
	@ClientAware
	public String toString() {
		return ClientRBRef.get(this.name().toLowerCase()).toString();
	}
}
