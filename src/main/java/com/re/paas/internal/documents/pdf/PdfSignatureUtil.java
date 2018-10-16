
package com.re.paas.internal.documents.pdf;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
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
import com.re.paas.internal.documents.SignatureInfo;
import com.re.paas.internal.utils.IOUtils;

public final class PdfSignatureUtil {

	private static final Logger LOG = LoggerFactory.get().getLog(PdfSignatureUtil.class);

	public PdfDocumentSpec generateSpec(Path path) throws IOException, CertificateException, NoSuchAlgorithmException,
			InvalidKeyException, NoSuchProviderException, SignatureException {

		InputStream in = Files.newInputStream(path);
		byte[] content = IOUtils.toByteArray(in);

		PdfDocumentSpec result = new PdfDocumentSpec();

		try (PDDocument document = PDDocument.load(content)) {

			result.setDocument(document);

			for (PDSignature sig : document.getSignatureDictionaries()) {

				COSDictionary sigDict = sig.getCOSObject();
				COSString sigBlock = (COSString) sigDict.getDictionaryObject(COSName.CONTENTS);

				byte[] signedContent = sig.getSignedContent(content);

				int[] byteRange = sig.getByteRange();

				if (byteRange.length != 4) {
					Exceptions.throwRuntime("Signature byteRange must have 4 items");
				}

				String subFilter = sig.getSubFilter();

				if (subFilter == null) {
					throw new IOException("Missing subfilter for cert dictionary");
				}

				PdfSignatureInfo sigInfo = new PdfSignatureInfo()
						.setCoversDocument(doesSignatureCoverDocument(byteRange, content.length, sigDict))
						.setSubFilter(subFilter);

				sigInfo.setSignDate(sig.getSignDate());

				switch (subFilter) {

				case PdfConstants.SUBFILTER_ADBE_PKCS7_DETACHED:

					// Note: Since this subfilter is detached, then <signedContent> is
					// required for signature verification, and hence the constructor:
					// CMSSignedData(signedContent, sigBlock) must be used

					verifyPKCS7(signedContent, sigBlock, sigInfo.setDetached(true));
					break;

				case PdfConstants.SUBFILTER_ADBE_PKCS7_SHA1: {

					verifyPKCS7(signedContent, sigBlock, sigInfo.setDetached(false));
					break;
				}

				case PdfConstants.SUBFILTER_ETSI_CADES_DETACHED: {

					verifyPKCS7(signedContent, sigBlock, sigInfo.setDetached(true));
					break;
				}

				case PdfConstants.SUBFILTER_ADBE_X509_RSA_SHA1: {
					Throwable t = new UnsupportedOperationException(
							"Subfilter: " + PdfConstants.SUBFILTER_ADBE_X509_RSA_SHA1 + " not yet supported");
					Exceptions.throwRuntime(t);
					break;
				}

				case PdfConstants.SUBFILTER_ETSI_RFC3161: {
					Throwable t = new UnsupportedOperationException(
							"Subfilter: " + PdfConstants.SUBFILTER_ETSI_RFC3161 + " not yet supported");
					Exceptions.throwRuntime(t);
					break;
				}

				default:

					System.err.println("Unknown certificate type: " + subFilter);
					break;
				}

				result.addSignature(sigInfo);

			}
		}

		return result;
	}

	/**
	 * Verify a PKCS7 signature.
	 * 
	 * @param signedContent byte sequence of the signed content
	 * @param sigBlock      the /Contents field as a COSString
	 * @param sig           the PDF signature (the /V dictionary)
	 */
	private static SignatureInfo verifyPKCS7(byte[] signedContent, COSString sigBlock, PdfSignatureInfo sigInfo) {

		try {

			// inspiration:
			// http://stackoverflow.com/a/26702631/535646
			// http://stackoverflow.com/a/9261365/535646

			// http://stackoverflow.com/questions/16662408

			CMSSignedData signedData = sigInfo.isDetached()
					? new CMSSignedData(new CMSProcessableByteArray(signedContent), sigBlock.getBytes())
					: new CMSSignedData(sigBlock.getBytes());

			Store certificatesStore = signedData.getCertificates();
			SignerInformationStore signers = signedData.getSignerInfos();

			SignerInformation signer = (SignerInformation) signers.getSigners().iterator().next();

			X509CertificateHolder certificateHolder = (X509CertificateHolder) certificatesStore
					.getMatches(signer.getSID()).iterator().next();

			String prov = DocumentConstants.BC_PROVIDER;

			X509Certificate cert = new JcaX509CertificateConverter().setProvider(prov)
					.getCertificate(certificateHolder);

			sigInfo.setCert(cert);

			try {

				// Verify that certificate is valid
				cert.checkValidity(sigInfo.getSignDate().getTime());
				sigInfo.setIsValid(true);

			} catch (CertificateExpiredException | CertificateNotYetValidException e) {
				sigInfo.setIsValid(false);
				return sigInfo;
			}

			if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(prov).build(cert))) {
				sigInfo.setIsValid(false);
				return sigInfo;
			}

			boolean hasChanged = Arrays.equals(signer.getContentDigest(),
					MessageDigest.getInstance(signer.getDigestAlgOID()).digest(signedContent));
			sigInfo.setHasChanged(hasChanged);

			boolean selfSigned = isSelfSigned(cert);
			sigInfo.setIsSelfSigned(selfSigned);

			// TODO verify that the CA authority of the certificate is trusted
			// TODO check certificate chain, revocation lists, timestamp...
			// TODO Add support for CRLs / OCSP
			boolean isTrusted = true;

			sigInfo.setIsTrusted(isTrusted);

		} catch (OperatorCreationException | CMSException | CertificateException | NoSuchAlgorithmException
				| NoSuchProviderException e) {
			Exceptions.throwRuntime(e);
		}

		return sigInfo;
	}

	public static Collection<? extends Certificate> getCertificates(COSDictionary sigDict) {

		COSString certString = (COSString) sigDict.getDictionaryObject(COSName.CONTENTS);

		try {

			byte[] certData = certString.getBytes();
			CertificateFactory factory = CertificateFactory.getInstance("X.509");

			ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
			Collection<? extends Certificate> certs = factory.generateCertificates(certStream);

			return certs;

		} catch (CertificateException e) {
			Exceptions.throwRuntime(e);
			return null;
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