
package com.re.paas.api.reporting.pdf.signature;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.internal.documents.DocumentConstants;
import com.re.paas.internal.utils.IOUtils;

/**
 * This will read a document from the filesystem, decrypt it and do something
 * with the signature.
 */
public final class PdfSignatureUtil {

	private static final Logger LOG = LoggerFactory.get().getLog(PdfSignatureUtil.class);
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	public void showSignature(Path path) throws IOException, CertificateException, NoSuchAlgorithmException,
			InvalidKeyException, NoSuchProviderException, SignatureException {

		InputStream in = Files.newInputStream(path);
		byte[] content = IOUtils.toByteArray(in);

		try (PDDocument document = PDDocument.load(content)) {

			for (PDSignature sig : document.getSignatureDictionaries()) {

				COSDictionary sigDict = sig.getCOSObject();
				COSString sigBlock = (COSString) sigDict.getDictionaryObject(COSName.CONTENTS);

				// download the signed content
				byte[] signedContent = sig.getSignedContent(content);

				int[] byteRange = sig.getByteRange();

				if (byteRange.length != 4) {
					Exceptions.throwRuntime("Signature byteRange must have 4 items");
				}

				System.out.println("Name:     " + sig.getName());
				System.out.println("Modified: " + sdf.format(sig.getSignDate().getTime()));
				System.out
						.println("Covers Document: " + doesSignatureCoverDocument(byteRange, content.length, sigDict));

				String subFilter = sig.getSubFilter();

				if (subFilter != null) {
					switch (subFilter) {

					case "adbe.pkcs7.detached":
						
						verifyPKCS7(sigBlock, sig);

						// TODO check certificate chain, revocation lists, timestamp...
						break;

					case "adbe.pkcs7.sha1": {
						// example: PDFBOX-1452.pdf
						COSString certString = (COSString) sigDict.getDictionaryObject(COSName.CONTENTS);
						
						byte[] certData = certString.getBytes();
						CertificateFactory factory = CertificateFactory.getInstance("X.509");
						
						ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
						Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
						System.out.println("certs=" + certs);
						
						verifyPKCS7(sigBlock, sig);

						// TODO check certificate chain, revocation lists, timestamp...
						break;
					}

					case "adbe.x509.rsa_sha1": {
						// example: PDFBOX-2693.pdf
						COSString certString = (COSString) sigDict.getDictionaryObject(COSName.getPDFName("Cert"));
						
						byte[] certData = certString.getBytes();
						CertificateFactory factory = CertificateFactory.getInstance("X.509");
						
						ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
						Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
						System.out.println("certs=" + certs);

						// TODO verify signature
						break;
					}

					default:
						System.err.println("Unknown certificate type: " + subFilter);
						break;
					}
				} else {
					throw new IOException("Missing subfilter for cert dictionary");
				}
			}
		}
	}

	/**
	 * This method checks whether the signature covers the whole document
	 * 
	 * @param byteRange
	 * @return
	 */
	private static boolean doesSignatureCoverDocument(int[] byteRange, long fileLen, COSDictionary sigDict) {

		long rangeMax = byteRange[2] + (long) byteRange[3];

		// multiply content length with 2 (because it is in hex in the PDF) and add 2
		// for < and >

		int contentLen = sigDict.getString(COSName.CONTENTS).length() * 2 + 2;

		if (fileLen != rangeMax || byteRange[0] != 0 || byteRange[1] + contentLen != byteRange[2]) {
			// a false result doesn't necessarily mean that the PDF is a fake
			// Signature does not cover whole document
			return false;
		} else {
			// Signature covers whole document
			return true;
		}
	}

	/**
	 * Verify a PKCS7 signature.
	 *
	 * @param sigBlock  the /Contents field as a COSString
	 * @param sig       the PDF signature (the /V dictionary)
	 */
	private static void verifyPKCS7(COSString sigBlock, PDSignature sig) {

		try {

			// inspiration:
			// http://stackoverflow.com/a/26702631/535646
			// http://stackoverflow.com/a/9261365/535646
			
			// http://stackoverflow.com/questions/16662408

			CMSSignedData signedData = new CMSSignedData(sigBlock.getBytes());

			Store certificatesStore = signedData.getCertificates();
			SignerInformationStore signers = signedData.getSignerInfos();

			@SuppressWarnings("unchecked")
			Iterator<SignerInformation> signersIterator = signers.getSigners().iterator();

			String prov = DocumentConstants.BC_PROVIDER;
			
			while (signersIterator.hasNext()) {
				SignerInformation signer = (SignerInformation) signersIterator.next();

				@SuppressWarnings("unchecked")
				Iterator<X509CertificateHolder> certIterator = certificatesStore.getMatches(signer.getSID()).iterator();

				X509CertificateHolder certificateHolder = (X509CertificateHolder) certIterator.next();

				X509Certificate cert = new JcaX509CertificateConverter()
						.setProvider(prov)
						.getCertificate(certificateHolder);

				/////////////////////////////////////////////////
				
				cert.checkValidity(sig.getSignDate().getTime());

				if (isSelfSigned(cert)) {
					
				} else {
					
				}
				
				/////////////////////////////////////////////////

				if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder()
						.setProvider(prov).build(cert))) {
					// verified;
				} else {
					// throw
				}
				
				
				signer.getContentDigest();
				
				signer.getDigestAlgOID();

			}

		} catch (OperatorCreationException | CMSException | CertificateException | NoSuchAlgorithmException
				| NoSuchProviderException e) {
			Exceptions.throwRuntime(e);
		}
	}

	// https://svn.apache.org/repos/asf/cxf/tags/cxf-2.4.1/distribution/src/main/release/samples/sts_issue_operation/src/main/java/demo/sts/provider/cert/CertificateVerifier.java

	/**
	 * Checks whether given X.509 certificate is self-signed.
	 */
	private static boolean isSelfSigned(X509Certificate cert)
			throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException {
		try {
			// Try to verify certificate signature with its own public key
			PublicKey key = cert.getPublicKey();
			cert.verify(key);
			return true;
		} catch (SignatureException | InvalidKeyException sigEx) {
			return false;
		}
	}

}