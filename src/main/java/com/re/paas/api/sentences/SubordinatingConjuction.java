package com.re.paas.api.sentences;

import com.re.paas.api.classes.ClientRBRef;

public enum SubordinatingConjuction {
	
	AFTER, ALTHOUGH, AS, AS_IF, AS_LONG_AS, AS_MUCH_AS, AS_SOON_AS, AS_THOUGH, BECAUSE, BEFORE, EVEN, EVEN_IF, EVEN_THOUGH, IF, IF_ONLY, IF_WHEN,
	IF_THEN, IN_AS_MUCH_AS, IN_ORDER_THAT,JUST_AS, LEST, NOW, NOW_SINCE, NOW_THAT, NOW_WHEN, ONCE, PROVIDED, PROVIDED_THAT, RATHER_THAN, SINCE,
	SO_THAT, SUPPOSING, THAN, THAT, THOUGH, TILL, UNLESS, UNTIL, WHEN, WHENEVER, WHERE, WHEREAS, WHERE_IF, WHEREVER, WHETHER, WHICH, WHILE, WHO,
	WHOEVER, WHY;
	
	@Override
	public String toString() {
		return ClientRBRef.get(this.name().toLowerCase()).toString();
	}
}
