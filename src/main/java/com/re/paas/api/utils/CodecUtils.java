package com.re.paas.api.utils;

/**
 * Codec internal utilities
 */
public enum CodecUtils {
    ;
    /**
     * Transforms the given string into the given destination byte array
     * truncating each character into a byte and skipping carriage returns and
     * line feeds if any.
     * 
     * @param singleOctets
     *            non-null string containing only single octet characters
     * @param dest
     *            destination byte array
     * 
     * @return the actual length of the destination byte array holding data
     * @throws IllegalArgumentException
     *             if the input string contains any multi-octet character
     */
    static int sanitize(final String singleOctets, byte[] dest) {
        final int capacity = dest.length;
        final char[] src = singleOctets.toCharArray();
        int limit=0;

        for (int i=0; i < capacity; i++) {
            final char c = src[i];
            
            if (c == '\r' || c == '\n' || c == ' ')
                continue;
            if (c > Byte.MAX_VALUE)
                throw new IllegalArgumentException("Invalid character found at position " + i + " for " + singleOctets);
            dest[limit++] = (byte)c;
        }
        return limit;
    }
    
    /**
     * Returns a byte array representing the given string,
     * truncating each character into a byte directly.
     * 
     * @throws IllegalArgumentException if the input string contains any multi-octet character
     */
    public static byte[] toBytesDirect(final String singleOctets) {
        final char[] src = singleOctets.toCharArray();
        final byte[] dest = new byte[src.length];
        
        for (int i=0; i < dest.length; i++) {
            final char c = src[i];
            
            if (c > Byte.MAX_VALUE)
                throw new IllegalArgumentException("Invalid character found at position " + i + " for " + singleOctets);
            dest[i] = (byte)c;
        }
        return dest;
    }

    /**
     * Returns a string representing the given byte array,
     * treating each byte as a single octet character.
     */
    public static String toStringDirect(final byte[] bytes) {
        final char[] dest = new char[bytes.length];
        int i=0;
        
        for (byte b: bytes)
            dest[i++] = (char)b;
        
        return new String(dest);
    }
    
    /** 
     * Sanity check the last decoded position is a possible value.
     * 
     * @throws IllegalArgumentException if the given decoded position is
     * not a possible value produced via the respective encoding 
     */
    static void sanityCheckLastPos(int pos, int mask) {
        if ((pos & mask) != 0) {
            throw new IllegalArgumentException
                ("Invalid last non-pad character detected");
        }
    }
}