package com.re.paas.internal.crytography.impl.signer;

import java.io.InputStream;

import com.re.paas.api.crytography.KeyStoreProperties;

public interface DocumentSigner {

	public KeyStoreProperties getKeyStoreProperties();
	
	public byte[] sign(InputStream content);
	
}
