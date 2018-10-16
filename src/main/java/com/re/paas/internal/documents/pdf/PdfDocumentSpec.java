package com.re.paas.internal.documents.pdf;

import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.re.paas.internal.documents.SignatureInfo;

public class PdfDocumentSpec {

	private PDDocument document;
	private List<SignatureInfo> signatures = new ArrayList<>();

	public PDDocument getDocument() {
		return document;
	}

	public PdfDocumentSpec setDocument(PDDocument document) {
		this.document = document;
		return this;
	}

	public List<SignatureInfo> getSignatures() {
		return signatures;
	}
	
	public PdfDocumentSpec addSignature(SignatureInfo signatureInfo) {
		this.signatures.add(signatureInfo);
		return this;
	}

	public PdfDocumentSpec setSignatures(List<SignatureInfo> signatures) {
		this.signatures = signatures;
		return this;
	}

}
