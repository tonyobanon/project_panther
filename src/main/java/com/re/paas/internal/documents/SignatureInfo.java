package com.re.paas.internal.documents;

import java.security.cert.Certificate;
import java.util.Calendar;

public class SignatureInfo {

	private Calendar signDate;

	private Certificate cert;

	private Boolean isValid;

	private Boolean isSelfSigned;
	private Boolean hasChanged;

	private Boolean isTrusted;
	

	public Calendar getSignDate() {
		return signDate;
	}

	public SignatureInfo setSignDate(Calendar signDate) {
		this.signDate = signDate;
		return this;
	}

	public Certificate getCert() {
		return cert;
	}

	public SignatureInfo setCert(Certificate cert) {
		this.cert = cert;
		return this;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	public SignatureInfo setIsValid(Boolean isValid) {
		this.isValid = isValid;
		return this;
	}

	public Boolean getIsSelfSigned() {
		return isSelfSigned;
	}

	public SignatureInfo setIsSelfSigned(Boolean isSelfSigned) {
		this.isSelfSigned = isSelfSigned;
		return this;
	}

	public Boolean getHasChanged() {
		return hasChanged;
	}

	public SignatureInfo setHasChanged(Boolean hasChanged) {
		this.hasChanged = hasChanged;
		return this;
	}

	public Boolean getIsTrusted() {
		return isTrusted;
	}

	public SignatureInfo setIsTrusted(Boolean isTrusted) {
		this.isTrusted = isTrusted;
		return this;
	}
	
}
