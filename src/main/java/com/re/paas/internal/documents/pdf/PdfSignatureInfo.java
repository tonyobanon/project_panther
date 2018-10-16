package com.re.paas.internal.documents.pdf;

import com.re.paas.internal.documents.SignatureInfo;

public class PdfSignatureInfo extends SignatureInfo {

	private boolean detached;
	private Boolean coversDocument;
	
	private String subFilter;
	

	public boolean isDetached() {
		return detached;
	}

	public PdfSignatureInfo setDetached(boolean detached) {
		this.detached = detached;
		return this;
	}
	
	public Boolean getCoversDocument() {
		return coversDocument;
	}

	public PdfSignatureInfo setCoversDocument(Boolean coversDocument) {
		this.coversDocument = coversDocument;
		return this;
	}

	public String getSubFilter() {
		return subFilter;
	}

	public PdfSignatureInfo setSubFilter(String subFilter) {
		this.subFilter = subFilter;
		return this;
	}

}
