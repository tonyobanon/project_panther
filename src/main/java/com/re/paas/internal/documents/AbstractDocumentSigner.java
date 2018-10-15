
package com.re.paas.internal.documents;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.util.Store;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.internal.crypto.KeyStoreConfig;
import com.re.paas.internal.crypto.SignContext;
import com.re.paas.internal.utils.IOUtils;

/**
 * A utility for signing a document with bouncy castle. A keystore can be
 * created with the java keytool, for example:
 *
 * {@code keytool -genkeypair -storepass 123456 -storetype pkcs12 -alias test -validity 365
 *        -v -keyalg RSA -keystore keystore.p12 }
 *
 */
public abstract class AbstractDocumentSigner implements DocumentSigner {

	private RSAPrivateKey privateKey;
	private X509Certificate certificate;

	private TSAClient tsaClient;

	/**
	 * Initialize the signature creator that should be used for the signature.
	 */
	public AbstractDocumentSigner() {

		KeyStoreConfig ksConfig = KeyStoreConfig.get();
		ksConfig.validateSignContext();

		KeyStore ks = ksConfig.getKeystore();
		SignContext ctx = ksConfig.getSignContext();

		this.tsaClient = ctx.getTsaClient();
		this.privateKey = ctx.getPrivateKey();

		try {
			this.certificate = (X509Certificate) ks.getCertificate(ctx.getCertAlias());
		} catch (KeyStoreException e) {
			Exceptions.throwRuntime(e);
		}
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

		byte[] token = this.tsaClient.getTimeStampToken(signer.getSignature());

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
	 * This method creates the PKCS #7 signature. The given InputStream contains the
	 * bytes that are given by the byte range.
	 */
	public byte[] sign(InputStream in) {

		try {

			
			String alg = DocumentConstants.SIGNATURE_ENCRYPTION_ALGORITHM;
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