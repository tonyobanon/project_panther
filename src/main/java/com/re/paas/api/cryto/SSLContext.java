package com.re.paas.api.cryto;

/**
 * This contains information required to setup SSL
 * @author Tony
 */
public class SSLContext {

	private String certAlias;
	
	public SSLContext(String certAlias) {
		this.certAlias = certAlias;
	}

	public String getCertAlias() {
		return certAlias;
	}

	public SSLContext setCertAlias(String certAlias) {
		this.certAlias = certAlias;
		return this;
	}
	
}
