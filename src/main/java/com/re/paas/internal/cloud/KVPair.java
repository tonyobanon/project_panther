package com.re.paas.internal.cloud;

import java.util.regex.Pattern;

public class KVPair {

	public static Pattern line() {
		return Pattern.compile("(\\p{Space})*[a-z]*(\\p{Space})*:(\\p{Space})*\\p{ASCII}+(\\p{Space})*");
	}

	public static Pattern separator() {
							return Pattern.compile("(\\p{Space})*:(\\p{Space})*");
	}

}
