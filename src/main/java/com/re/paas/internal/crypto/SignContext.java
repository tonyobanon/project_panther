package com.re.paas.internal.crypto;

import com.re.paas.internal.documents.TSAClient;

/**
 * This contains information required for signing documents. This implementation
 * assumes that the alias is a KeyStore.PrivateKeyEntry
 * @author Tony
 */
public class SignContext {

	private TSAClient tsaClient;

	private String certAlias;

	private String keyAlias;
	private String keyAliasPassword;

	public TSAClient getTsaClient() {
		return tsaClient;
	}

	public String getCertAlias() {
		return certAlias;
	}

	public String getKeyAlias() {
		return keyAlias;
	}

	public String getKeyAliasPassword() {
		return keyAliasPassword;
	}

}
