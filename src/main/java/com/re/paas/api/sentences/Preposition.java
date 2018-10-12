package com.re.paas.api.sentences;

import com.re.paas.api.classes.ClientRBRef;

/**
 * In English grammar, a preposition is a statement or assertion that expresses a judgment
 * */
public enum Preposition {
 WITH, AT, FROM, INTO, DURING, INCLUDING, UNTIL, AGAINST, AMONG, THROUGHOUT, DESPITE, TOWARDS, UPON, CONCERNING, OF, TO, IN, FOR, ON, BY, ABOUT,
 LIKE, THROUGH, OVER, BEFORE, BETWEEN, AFTER, SINCE, WITHOUT, UNDER, WITHIN, ALONG, FOLLOWING, ACROSS, BEHIND, BEYOND, PLUS, EXCEPT, BUT, UP,
 OUT, AROUND, DOWN, OFF, ABOVE, NEAR;
	
	@Override
	public String toString() {
		return ClientRBRef.get(this.name().toLowerCase()).toString();
	}
	
}
