package com.re.paas.api.crytography;

import java.security.interfaces.RSAPrivateKey;

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

	public SignContext setTsaClient(TSAClient tsaClient) {
		this.tsaClient = tsaClient;
		return this;
	}

	public SignContext setCertAlias(String certAlias) {
		this.certAlias = certAlias;
		return this;
	}

	public SignContext setPrivateKey(RSAPrivateKey privateKey) {
		this.privateKey = privateKey;
		return this;
	}

}
