package com.re.paas.internal.utils;


import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.utils.Base64;

/**
 * A Base 64 codec API.
 *
 * See http://www.ietf.org/rfc/rfc4648.txt
 *
 */
public class Base64Impl implements Base64 {
    ;
    private static final Logger LOG = Logger.get(Base64Impl.class);
    private static final Base64Codec codec = new Base64Codec();

    static {
        Map<String,String> inconsistentJaxbImpls = new HashMap<String, String>();
        inconsistentJaxbImpls.put("org.apache.ws.jaxme.impl.JAXBContextImpl", "Apache JaxMe");

        try {
            String className = JAXBContext.newInstance().getClass().getName();
            if (inconsistentJaxbImpls.values().contains(className)) {
                LOG.warn("A JAXB implementation known to produce base64 encodings that are " +
                        "inconsistent with the reference implementation has been detected. The " +
                        "results of the encodeAsString() method may be incorrect. Implementation: " +
                        inconsistentJaxbImpls.get(className));
            }
        } catch (JAXBException ignored) {
        }
    }

    /**
     * Returns a base 64 encoded string of the given bytes.
     */
    public String encodeAsString(byte ... bytes) {
        if (bytes == null)
            return null;
        try {
            return DatatypeConverter.printBase64Binary(bytes);
        } catch (NullPointerException ex) {
            // https://netbeans.org/bugzilla/show_bug.cgi?id=224923
            // https://issues.apache.org/jira/browse/CAMEL-4893

            // Note the converter should eventually be initialized and printBase64Binary should start working again
        	Exceptions.throwRuntime("Recovering from JAXB bug: https://netbeans.org/bugzilla/show_bug.cgi?id=224923", ex);
        }
        
        return bytes.length == 0 ? "" : CodecUtils.toStringDirect(codec.encode(bytes));
    }

    /**
     * Returns a 64 encoded byte array of the given bytes.
     */
    public byte[] encode(byte[] bytes) { return bytes == null || bytes.length == 0 ? bytes : codec.encode(bytes); }

    /**
     * Decodes the given base 64 encoded string,
     * skipping carriage returns, line feeds and spaces as needed.
     */
    public byte[] decode(String b64) {
        if (b64 == null)
            return null;
        if (b64.length() == 0)
            return new byte[0];
        byte[] buf = new byte[b64.length()];
        int len = CodecUtils.sanitize(b64, buf);
        return codec.decode(buf, len);
    }

    /**
     * Decodes the given base 64 encoded bytes.
     */
    public byte[] decode(byte[] b64) { return b64 == null || b64.length == 0 ? b64 :  codec.decode(b64, b64.length); }
}

