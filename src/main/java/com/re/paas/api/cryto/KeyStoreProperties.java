package com.re.paas.api.cryto;

import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.Platform;

public class KeyStoreProperties {

	private boolean keyStoreEnabled;
	private KeyStore keyStore;
	
	private String keyStorePassword;


	private SSLContext sslContext;
	private SignContext signContext;


	/**
	 * This validates the certificate used for this signing context.
	 */
	public void validateSignContext() {

		if (signContext != null) {
			validateCert(signContext.getCertAlias());
		}
	}

	/**
	 * This validates the certificate used for this SSL context.
	 */
	public void validateSSLContext() {

		if (sslContext != null) {
			validateCert(sslContext.getCertAlias());
		}
	}

	public void validateCert(String certAlias) {
		try {
			Certificate cert = keyStore.getCertificate(certAlias);
			if (cert instanceof X509Certificate) {
				// avoid expired certificate
				((X509Certificate) cert).checkValidity();
			}

		} catch (KeyStoreException | CertificateExpiredException | CertificateNotYetValidException e) {
			Exceptions.throwRuntime(e);
		}
	}

	
	public KeyStoreProperties setKeyStoreEnabled(boolean keyStoreEnabled) {
		this.keyStoreEnabled = keyStoreEnabled;
		return this;
	}

	public KeyStoreProperties setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
		return this;
	}

	public KeyStoreProperties setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
		return this;
	}

	public KeyStoreProperties setSslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
		return this;
	}

	public KeyStoreProperties setSignContext(SignContext signContext) {
		this.signContext = signContext;
		return this;
	}

	public KeyStore getKeystore() {
		return keyStore;
	}

	public boolean keyStoreEnabled() {
		return keyStoreEnabled;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	public SignContext getSignContext() {
		return signContext;
	}

	public static Path getKeyStorePath() {
		
		
		
		return Platform.getResourcePath().resolve("/crypto/keystore.p12");
	}

	public static String getType() {
		return "PKCS12";
	}

}
