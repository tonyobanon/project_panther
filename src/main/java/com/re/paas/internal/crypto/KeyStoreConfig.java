package com.re.paas.internal.crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.Platform;
import com.re.paas.internal.classes.ResourceFile;

public class KeyStoreConfig extends ResourceFile {

	private static KeyStoreConfig instance;
	
	private boolean keyStoreEnabled;
	private String keyStorePassword;
	
	private KeyStore keyStore;
	
	private SSLContext sslContext;
	private SignContext signContext;

	public KeyStoreConfig() {
		super("cryptoProfile.json");
	}
	
	public static KeyStoreConfig get() {
		if (instance != null) {
			return instance;
		}
		
		instance = new KeyStoreConfig().load(KeyStoreConfig.class);
		
		if(instance.keyStoreEnabled()) {
			instance.loadKeystore();
		}
		
		return instance;
	}
	
	public void validateSignContext() {
		
		if(keyStore == null) {
			this.loadKeystore();
		}
		
		if(signContext != null) {
			validateCert(signContext.getCertAlias());
		}

	}
	
	public void validateSSLContext() {

		if(keyStore == null) {
			this.loadKeystore();
		}
		
		if(sslContext != null) {
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

	public void loadKeystore() {
		try {
			keyStore = KeyStore.getInstance(getType());
			keyStore.load(Files.newInputStream(getKeyStorePath()), keyStorePassword.toCharArray());
		} catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			Exceptions.throwRuntime(e);
		}
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
