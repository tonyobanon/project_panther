package com.re.paas.internal.crypto.impl;

import java.nio.file.Path;

import com.re.paas.api.cryto.KeyStoreProperties;
import com.re.paas.internal.Platform;

public class DefaultKeyStoreProperties extends KeyStoreProperties {

	private final String type;
	private final Path keyStorePath;
	
	public DefaultKeyStoreProperties() {
		super();
		this.type = "PKCS12";
		this.keyStorePath = Platform.getResourcePath().resolve("/crypto/keystore.p12");
	}

	@Override
	public Path getKeyStorePath() {
		return keyStorePath;
	}

	@Override
	public String getType() {
		return type;
	}

}
