package com.re.paas.internal.documents.pdf;

public class PdfConstants {

	public static final String SUBFILTER_ADBE_PKCS7_DETACHED = "adbe.pkcs7.detached";
	public static final String SUBFILTER_ADBE_PKCS7_SHA1 = "adbe.pkcs7.sha1";
	public static final String SUBFILTER_ADBE_X509_RSA_SHA1 = "adbe.x509.rsa_sha1";
	public static final String SUBFILTER_ETSI_CADES_DETACHED = "ETSI.CAdES.detached";
	public static final String SUBFILTER_ETSI_RFC3161 = "ETSI.RFC3161";
	
	
	// We are using SHA1 message digest for or full PDF/A-1 compatibility, 
	// we don't use SHA256 since it didn't exist in PDF 1.4 (on which PDF/A-1 is based)
	public static final String SIGNATURE_ENCRYPTION_ALGORITHM = "SHA1WithRSA";

}
