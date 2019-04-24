package com.re.paas.api.utils;

import com.re.paas.api.designpatterns.Singleton;

public interface Base64 {
	
	public static Base64 get() {
		return Singleton.get(Base64.class);
	}
	
	 /**
     * Returns a base 64 encoded string of the given bytes.
     */
    String encodeAsString(byte ... bytes);

    /**
     * Returns a 64 encoded byte array of the given bytes.
     */
    byte[] encode(byte[] bytes);

    /**
     * Decodes the given base 64 encoded string,
     * skipping carriage returns, line feeds and spaces as needed.
     */
    byte[] decode(String b64);

    /**
     * Decodes the given base 64 encoded bytes.
     */
    byte[] decode(byte[] b64);
}