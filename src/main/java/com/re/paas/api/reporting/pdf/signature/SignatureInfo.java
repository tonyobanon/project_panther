package com.re.paas.api.reporting.pdf.signature;

import java.security.cert.Certificate;
import java.util.Calendar;

public class SignatureInfo {

	private Calendar signDate;
	private boolean coversDocument;
	
	private String subFilter;
	private Certificate cert;
	private boolean isSelfSigned;
	
	
	
}
