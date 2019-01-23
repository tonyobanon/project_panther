package com.re.paas.internal.crypto.impl.signer;

import java.io.InputStream;
import java.io.OutputStream;

import com.re.paas.api.cryto.KeyStoreProperties;

public interface DocumentSigner {

	public KeyStoreProperties getKeyStoreProperties();
	
	public byte[] sign(InputStream content);
	
}
