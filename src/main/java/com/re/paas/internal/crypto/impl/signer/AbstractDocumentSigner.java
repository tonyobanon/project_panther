
package com.re.paas.internal.crypto.impl.signer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

import org.bouncycastle.util.Store;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.cryto.CryptoProvider;
import com.re.paas.api.cryto.KeyStoreProperties;
import com.re.paas.api.cryto.SignContext;
import com.re.paas.api.cryto.TSAClient;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.utils.IOUtils;
import com.re.paas.internal.documents.DocumentConstants;

/**
 * A utility for signing a document with bouncy castle. A keystore can be
 * created with the java keytool, for example:
 *
 * {@code keytool -genkeypair -storepass 123456 -storetype pkcs12 -alias test -validity 365
 *        -v -keyalg RSA -keystore keystore.p12 }
 *
 */
public abstract class AbstractDocumentSigner implements DocumentSigner {

    private static final Logger LOG = LoggerFactory.get().getLog(AbstractDocumentSigner.class);

	private final KeyStoreProperties keyStoreProperties;
	
	private RSAPrivateKey privateKey;
	private X509Certificate certificate;

	private TSAClient tsaClient;

	/**
	 * Initialize the signature creator that should be used for the signature.
	 */
	public AbstractDocumentSigner() {

		this.keyStoreProperties = CryptoProvider.get().getKeyStoreProperties();
		this.keyStoreProperties.validateSignContext();

		KeyStore ks = this.keyStoreProperties.getKeystore();
		SignContext ctx = this.keyStoreProperties.getSignContext();

		this.tsaClient = ctx.getTsaClient();
		this.privateKey = ctx.getPrivateKey();

		try {
			this.certificate = (X509Certificate) ks.getCertificate(ctx.getCertAlias());
		} catch (KeyStoreException e) {
			Exceptions.throwRuntime(e);
		}
	}
	
	@Override
	public KeyStoreProperties getKeyStoreProperties() {
		return keyStoreProperties;
	}

	/**
	 * We just extend CMS signed Data
	 *
	 * @param signedData Â´Generated CMS signed data
	 * @return CMSSignedData Extended CMS signed data
	 * @throws IOException
	 * @throws                          org.bouncycastle.tsp.TSPException
	 * @throws NoSuchAlgorithmException
	 */
	private CMSSignedData signTimeStamps(CMSSignedData signedData)
			throws IOException, TSPException, NoSuchAlgorithmException {

		SignerInformationStore signerStore = signedData.getSignerInfos();
		List<SignerInformation> newSigners = new ArrayList<>();

		@SuppressWarnings("unchecked")
		Collection<SignerInformation> signers = signerStore.getSigners();

		for (SignerInformation signer : signers) {
			newSigners.add(signTimeStamp(signer));
		}

		return CMSSignedData.replaceSigners(signedData, new SignerInformationStore(newSigners));
	}

	/**
	 * We are extending CMS Signature
	 *
	 * @param signer information about signer
	 * @return information about SignerInformation
	 * @throws NoSuchAlgorithmException
	 */
	private SignerInformation signTimeStamp(SignerInformation signer)
			throws IOException, TSPException, NoSuchAlgorithmException {

		AttributeTable unsignedAttributes = signer.getUnsignedAttributes();

		ASN1EncodableVector vector = new ASN1EncodableVector();
		if (unsignedAttributes != null) {
			vector = unsignedAttributes.toASN1EncodableVector();
		}

		byte[] token = getTimeStampToken(this.tsaClient, signer.getSignature());

		ASN1ObjectIdentifier oid = PKCSObjectIdentifiers.id_aa_signatureTimeStampToken;
		ASN1Encodable signatureTimeStamp = new Attribute(oid, new DERSet(ASN1Primitive.fromByteArray(token)));

		vector.add(signatureTimeStamp);
		Attributes signedAttributes = new Attributes(vector);

		SignerInformation newSigner = SignerInformation.replaceUnsignedAttributes(signer,
				new AttributeTable(signedAttributes));

		// TODO can this actually happen?
		if (newSigner == null) {
			return signer;
		}

		return newSigner;
	}
	
	/**
    *
    * @param messageImprint imprint of message contents
    * @return the encoded time stamp token
    * @throws IOException if there was an error with the connection or data from the TSA server,
    *                     or if the time stamp response could not be validated
    * @throws NoSuchAlgorithmException 
    */
	private byte[] getTimeStampToken(TSAClient client, byte[] messageImprint) throws IOException, NoSuchAlgorithmException {

    	MessageDigest digest = MessageDigest.getInstance(client.getDigest());
    			
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
        byte[] tsaResponse = getTSAResponse(client, request.getEncoded());

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
    private byte[] getTSAResponse(TSAClient client, byte[] request) throws IOException
    {
        LOG.debug("Opening connection to TSA server");

        // todo: support proxy servers
        URLConnection connection = new URL(client.getUrl()).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "application/timestamp-query");

        LOG.debug("Established connection to TSA server");

        if (client.getUsername() != null && client.getPassword() != null && !client.getUsername().isEmpty() && !client.getPassword().isEmpty())
        {
            connection.setRequestProperty(client.getUsername(), client.getPassword());
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
    
	/**
	 * This method creates the PKCS #7 signature. The given InputStream contains the
	 * bytes that are given by the byte range.
	 */
	public byte[] sign(InputStream in) { 

		try {
			
			String alg = PdfSignatureConstants.SIGNATURE_ENCRYPTION_ALGORITHM;
			String prov = DocumentConstants.BC_PROVIDER;

			byte[] contents = IOUtils.toByteArray(in);

			// Sign
			Signature signature = Signature.getInstance(alg, prov);
			signature.initSign(this.privateKey);
			signature.update(contents);

			// Build CMS

			List<Certificate> certList = new ArrayList<>();
			certList.add(this.certificate);

			Store certs = new JcaCertStore(certList);

			CMSTypedData msg = new CMSProcessableByteArray(signature.sign());

			CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

			ContentSigner signer = new JcaContentSignerBuilder(alg).setProvider(prov).build(this.privateKey);

			gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
					new JcaDigestCalculatorProviderBuilder().setProvider(prov).build())
					.build(signer, this.certificate));

			gen.addCertificates(certs);

			CMSSignedData signedData = gen.generate(msg, false);

			if (this.tsaClient != null) {
				signedData = signTimeStamps(signedData);
			}

			return signedData.getEncoded();

		} catch (GeneralSecurityException | CMSException | TSPException | OperatorCreationException | IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}
}