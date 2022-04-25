package com.re.paas.api.fusion;

import com.re.paas.api.Singleton;

public interface MimeTypeDetector {

	String detect(String fileName);
	
	public static MimeTypeDetector get() {
		return Singleton.get(MimeTypeDetector.class);
	}
	
}
