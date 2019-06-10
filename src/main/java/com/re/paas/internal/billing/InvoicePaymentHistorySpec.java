package com.re.paas.internal.billing;

import com.re.paas.api.classes.ClientRBRef;

public class InvoicePaymentHistorySpec extends InvoicePaymentSpec {

	private ClientRBRef additionalInfo;
	private InvoiceStatus previousStatus;

	public ClientRBRef getAdditionalInfo() {
		return additionalInfo;
	}

	public InvoicePaymentHistorySpec setAdditionalInfo(ClientRBRef additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}

	public InvoiceStatus getPreviousStatus() {
		return previousStatus;
	}

	public InvoicePaymentHistorySpec setPreviousStatus(InvoiceStatus previousStatus) {
		this.previousStatus = previousStatus;
		return this;
	}

}
