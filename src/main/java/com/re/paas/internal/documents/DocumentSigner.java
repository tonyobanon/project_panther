package com.re.paas.internal.documents;

import java.io.InputStream;

public interface DocumentSigner {

	public byte[] sign(InputStream content);
	
}
