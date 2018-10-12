
package com.re.paas.api.reporting.pdf.signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;

/**
 * This will read a document from the filesystem, decrypt it and do something with the signature.
 */
public final class ShowSignatureSample
{
	
	private static final Logger LOG = LoggerFactory.get().getLog(ShowSignatureSample.class);
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    

    public void showSignature(Path path) throws IOException, CertificateException,
                                                     NoSuchAlgorithmException, InvalidKeyException,
                                                     NoSuchProviderException, SignatureException
    {
  
    	InputStream in = Files.newInputStream(path);
            
            try (PDDocument document = PDDocument.load(in))
            {
            	
                for (PDSignature sig : document.getSignatureDictionaries())
                {
                    COSDictionary sigDict = sig.getCOSObject();
                    COSString contents = (COSString) sigDict.getDictionaryObject(COSName.CONTENTS);

                    // download the signed content
                    byte[] buf = sig.getSignedContent(in);

                    int[] byteRange = sig.getByteRange();
                    if (byteRange.length != 4)
                    {
                        Exceptions.throwRuntime("Signature byteRange must have 4 items");
                    }
                    else
                    {
                    	
                    	File infile = null;
                        long fileLen = infile.length();
                        long rangeMax = byteRange[2] + (long) byteRange[3];
                        // multiply content length with 2 (because it is in hex in the PDF) and add 2 for < and >
                        int contentLen = sigDict.getString(COSName.CONTENTS).length() * 2 + 2;
                        if (fileLen != rangeMax || byteRange[0] != 0 || byteRange[1] + contentLen != byteRange[2])
                        {
                            // a false result doesn't necessarily mean that the PDF is a fake
                            System.out.println("Signature does not cover whole document");
                        }
                        else
                        {
                            System.out.println("Signature covers whole document");
                        }
                    }

                    System.out.println("Name:     " + sig.getName());
                    System.out.println("Modified: " + sdf.format(sig.getSignDate().getTime()));
                    
                    String subFilter = sig.getSubFilter();
                    if (subFilter != null)
                    {
                        switch (subFilter)
                        {
                        
                            case "adbe.pkcs7.detached":
                                verifyPKCS7(buf, contents, sig);

                                //TODO check certificate chain, revocation lists, timestamp...
                                break;
                            case "adbe.pkcs7.sha1":
                            {
                                // example: PDFBOX-1452.pdf
                                COSString certString = (COSString) sigDict.getDictionaryObject(
                                        COSName.CONTENTS);
                                byte[] certData = certString.getBytes();
                                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                                ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
                                Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
                                System.out.println("certs=" + certs);
                                byte[] hash = MessageDigest.getInstance("SHA1").digest(buf);
                                verifyPKCS7(hash, contents, sig);

                                //TODO check certificate chain, revocation lists, timestamp...
                                break;
                            }
                            case "adbe.x509.rsa_sha1":
                            {
                                // example: PDFBOX-2693.pdf
                                COSString certString = (COSString) sigDict.getDictionaryObject(
                                        COSName.getPDFName("Cert"));
                                byte[] certData = certString.getBytes();
                                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                                ByteArrayInputStream certStream = new ByteArrayInputStream(certData);
                                Collection<? extends Certificate> certs = factory.generateCertificates(certStream);
                                System.out.println("certs=" + certs);

                                //TODO verify signature
                                break;
                            }
                            default:
                                System.err.println("Unknown certificate type: " + subFilter);
                                break;
                        }
                    }
                    else
                    {
                        throw new IOException("Missing subfilter for cert dictionary");
                    }
                }
            }
            catch (CMSException | OperatorCreationException ex)
            {
                throw new IOException(ex);
            }
    }

    /**
     * Verify a PKCS7 signature.
     *
     * @param byteArray the byte sequence that has been signed
     * @param contents the /Contents field as a COSString
     * @param sig the PDF signature (the /V dictionary)
     * @throws CertificateException
     * @throws CMSException
     * @throws StoreException
     * @throws OperatorCreationException
     */
    private void verifyPKCS7(byte[] byteArray, COSString contents, PDSignature sig)
            throws CMSException, CertificateException, StoreException, OperatorCreationException,
                   NoSuchAlgorithmException, NoSuchProviderException
    {
    	
        // inspiration:
        // http://stackoverflow.com/a/26702631/535646
        // http://stackoverflow.com/a/9261365/535646
    	
        CMSProcessable signedContent = new CMSProcessableByteArray(byteArray);
        CMSSignedData signedData = new CMSSignedData(signedContent, contents.getBytes());
        
        Store certificatesStore = signedData.getCertificates();
        
        @SuppressWarnings("unchecked")
		Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();
        
        SignerInformation signerInformation = signers.iterator().next();
        Collection<?> matches = certificatesStore.getMatches(signerInformation.getSID());
        
        X509CertificateHolder certificateHolder = (X509CertificateHolder) matches.iterator().next();
        X509Certificate certFromSignedData = new JcaX509CertificateConverter().getCertificate(certificateHolder);
        
        System.out.println("certFromSignedData: " + certFromSignedData);
        certFromSignedData.checkValidity(sig.getSignDate().getTime());
        
        if (isSelfSigned(certFromSignedData))
        {
            System.err.println("Certificate is self-signed, LOL!");
        }
        else
        {
            System.out.println("Certificate is not self-signed");
            // todo rest of chain
        }

        if (signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().build(certFromSignedData)))
        {
            System.out.println("Signature verified");
        }
        else
        {
            System.out.println("Signature verification failed");
        }
    }

    // https://svn.apache.org/repos/asf/cxf/tags/cxf-2.4.1/distribution/src/main/release/samples/sts_issue_operation/src/main/java/demo/sts/provider/cert/CertificateVerifier.java

    /**
     * Checks whether given X.509 certificate is self-signed.
     */
    private boolean isSelfSigned(X509Certificate cert)
            throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException
    {
        try
        {
            // Try to verify certificate signature with its own public key
            PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        }
        catch (SignatureException | InvalidKeyException sigEx)
        {
            return false;
        }
    }

}