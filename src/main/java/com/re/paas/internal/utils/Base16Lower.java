package com.re.paas.internal.utils;

/**
 * A Base 16 codec API, which encodes into hex string in lower case.
 *
 * See http://www.ietf.org/rfc/rfc4648.txt
 */
public enum Base16Lower {
    ;
    private static final Base16Codec codec = new Base16Codec(false);

    /**
     * Returns a base 16 encoded string (in lower case) of the given bytes.
     */
    public static String encodeAsString(byte ... bytes) {
        if (bytes == null)
            return null;
        return bytes.length == 0 ? "" : CodecUtils.toStringDirect(codec.encode(bytes));
    }

    /**
     * Returns a base 16 encoded byte array of the given bytes.
     */
    public static byte[] encode(byte[] bytes) { return bytes == null || bytes.length == 0 ? bytes : codec.encode(bytes); }

    /**
     * Decodes the given base 16 encoded string,
     * skipping carriage returns, line feeds and spaces as needed.
     */
    public static byte[] decode(String b16) {
        if (b16 == null)
            return null;
        if (b16.length() == 0)
            return new byte[0];
        byte[] buf = new byte[b16.length()];
        int len = CodecUtils.sanitize(b16, buf);
        return codec.decode(buf, len);
    }

    /**
     * Decodes the given base 16 encoded bytes.
     */
    public static byte[] decode(byte[] b16) { return b16 == null || b16.length == 0 ? b16 :  codec.decode(b16, b16.length); }
}
