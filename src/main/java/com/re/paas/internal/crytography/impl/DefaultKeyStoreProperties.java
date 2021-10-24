package com.re.paas.internal.crytography.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.crytography.KeyStoreProperties;
import com.re.paas.api.infra.filesystem.NativeFileSystem;

public class DefaultKeyStoreProperties extends KeyStoreProperties {

	private final String type;
	private final Path keyStorePath;
	
	public DefaultKeyStoreProperties() {
		super();
		this.type = "PKCS12";
		this.keyStorePath = NativeFileSystem.get().getResourcePath().resolve("/crypto/keystore.p12");
		
		if (!Files.exists(keyStorePath)) {
			try {
				Files.createDirectories(keyStorePath);
			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}
		}
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
