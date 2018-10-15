package com.re.paas.internal.crypto;

import java.security.interfaces.RSAPrivateKey;

import com.re.paas.internal.documents.TSAClient;

/**
 * This contains information required for signing documents.
 * 
 * @author Tony
 */
public class SignContext {

	private TSAClient tsaClient;

	private String certAlias;
	private RSAPrivateKey privateKey;

	public TSAClient getTsaClient() {
		return tsaClient;
	}

	public String getCertAlias() {
		return certAlias;
	}

	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}

}
