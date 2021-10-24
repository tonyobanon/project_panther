package com.re.paas.internal.crytography.impl;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.re.paas.api.crytography.CryptoProvider;
import com.re.paas.api.crytography.KeyStoreProperties;
import com.re.paas.internal.crytography.impl.signer.PdfSignerUtil;

public class DefaultCryptoProvider implements CryptoProvider {

	private final KeyStoreProperties keyStoreProperties;

	private Map<String, BiConsumer<Path, Path>> documentSigners = new HashMap<>();

	public DefaultCryptoProvider(KeyStoreProperties keyStoreProperties) {
		this.keyStoreProperties = keyStoreProperties;
		this.registerSigners();
	}

	@Override
	public KeyStoreProperties getKeyStoreProperties() {
		return keyStoreProperties;
	}

	private BiConsumer<Path, Path> getSigner(Path in) {

		String pathString = in.toString();

		for (Entry<String, BiConsumer<Path, Path>> e : documentSigners.entrySet()) {

			if (pathString.endsWith("." + e.getKey())) {
				return e.getValue();
			}
		}
		return null;
	}

	@Override
	public void signDocument(Path in, Path out) {
		
		BiConsumer<Path, Path> signer = getSigner(in);
		signer.accept(in, out);
	}


	private void registerSigners() {
		this.documentSigners.put("pdf", PdfSignerUtil::signPdf);
	}
}
