package com.re.paas.api.cryto;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public interface CryptoProvider {
	
	public static CryptoProvider get() {
		return CryptoAdapter.getDelegate().getProvider();
	}
	
	public KeyStoreProperties getKeyStoreProperties();

	public void signDocument(Path in, Path out);
	
}
