
package com.re.paas.internal.documents;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;

import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.internal.utils.IOUtils;

/**
 * Time Stamping Authority (TSA) Client [RFC 3161].
 */
public class TSAClient
{
    private static final Logger LOG = LoggerFactory.get().getLog(TSAClient.class);

    private String url;
    private String username;
    private String password;
    private String digest;

    public TSAClient() {
	}
    
    /**
     *
     * @param url the URL of the TSA service
     * @param username user name of TSA
     * @param password password of TSA
     * @param digest the message digest to use
     */
    public TSAClient(String url, String username, String password, String digest)
    {
        this.url = url;
        this.username = username;
        this.password = password;
        this.digest = digest;
    }

    /**
     *
     * @param messageImprint imprint of message contents
     * @return the encoded time stamp token
     * @throws IOException if there was an error with the connection or data from the TSA server,
     *                     or if the time stamp response could not be validated
     * @throws NoSuchAlgorithmException 
     */
    public byte[] getTimeStampToken(byte[] messageImprint) throws IOException, NoSuchAlgorithmException
    {

    	MessageDigest digest = MessageDigest.getInstance(this.digest);
    			
    	digest.reset();
        byte[] hash = digest.digest(messageImprint);

        // 32-bit cryptographic nonce
        SecureRandom random = new SecureRandom();
        int nonce = random.nextInt();

        // generate TSA request
        TimeStampRequestGenerator tsaGenerator = new TimeStampRequestGenerator();
        tsaGenerator.setCertReq(true);
        ASN1ObjectIdentifier oid = getHashObjectIdentifier(digest.getAlgorithm());
        TimeStampRequest request = tsaGenerator.generate(oid, hash, BigInteger.valueOf(nonce));

        // get TSA response
        byte[] tsaResponse = getTSAResponse(request.getEncoded());

        TimeStampResponse response;
        try
        {
            response = new TimeStampResponse(tsaResponse);
            response.validate(request);
        }
        catch (TSPException e)
        {
            throw new IOException(e);
        }
        
        TimeStampToken token = response.getTimeStampToken();
        if (token == null)
        {
            throw new IOException("Response does not have a time stamp token");
        }

        return token.getEncoded();
    }

    // gets response data for the given encoded TimeStampRequest data
    // throws IOException if a connection to the TSA cannot be established
    private byte[] getTSAResponse(byte[] request) throws IOException
    {
        LOG.debug("Opening connection to TSA server");

        // todo: support proxy servers
        URLConnection connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/timestamp-query");

        LOG.debug("Established connection to TSA server");

        if (username != null && password != null && !username.isEmpty() && !password.isEmpty())
        {
            connection.setRequestProperty(username, password);
        }

        // read response
        OutputStream output = null;
        try
        {
            output = connection.getOutputStream();
            output.write(request);
        }
        finally
        {
            IOUtils.closeQuietly(output);
        }

        LOG.debug("Waiting for response from TSA server");

        InputStream input = null;
        byte[] response;
        try
        {
            input = connection.getInputStream();
            response = IOUtils.toByteArray(input);
        }
        finally
        {
            IOUtils.closeQuietly(input);
        }

        LOG.debug("Received response from TSA server");

        return response;
    }

    // returns the ASN.1 OID of the given hash algorithm
    private ASN1ObjectIdentifier getHashObjectIdentifier(String algorithm)
    {
        switch (algorithm)
        {
            case "MD2":
                return new ASN1ObjectIdentifier(PKCSObjectIdentifiers.md2.getId());
            case "MD5":
                return new ASN1ObjectIdentifier(PKCSObjectIdentifiers.md5.getId());
            case "SHA-1":
                return new ASN1ObjectIdentifier(OIWObjectIdentifiers.idSHA1.getId());
            case "SHA-224":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha224.getId());
            case "SHA-256":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha256.getId());
            case "SHA-384":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha384.getId());
            case "SHA-512":
                return new ASN1ObjectIdentifier(NISTObjectIdentifiers.id_sha512.getId());
            default:
                return new ASN1ObjectIdentifier(algorithm);
        }
    }
}