package com.re.paas.internal.classes.spec;

import java.util.HashMap;
import java.util.Map;

public class ClientSignature {

	private Map<ClientSignatureType, String> values = new HashMap<ClientSignatureType, String>();

	public Map<ClientSignatureType, String> getValues() {
		return values;
	}

	public ClientSignature setValues(Map<ClientSignatureType, String> values) {
		this.values = values;
		return this;
	}

}
