package com.re.paas.internal.billing;

import java.util.Date;

import com.re.paas.api.classes.ClientRBRef;

public class InvoicePaymentSpec {

	String invoiceId;

	String reference;
	
	String gatewayReference;

	PaymentStatus status;

	ClientRBRef message;

	Date dateCreated;

	Date dateUpdated;

	public String getInvoiceId() {
		return invoiceId;
	}

	public InvoicePaymentSpec setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public InvoicePaymentSpec setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public String getGatewayReference() {
		return gatewayReference;
	}

	public InvoicePaymentSpec setGatewayReference(String gatewayReference) {
		this.gatewayReference = gatewayReference;
		return this;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public InvoicePaymentSpec setStatus(PaymentStatus status) {
		this.status = status;
		return this;
	}

	public ClientRBRef getMessage() {
		return message;
	}

	public InvoicePaymentSpec setMessage(ClientRBRef message) {
		this.message = message;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public InvoicePaymentSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public InvoicePaymentSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
}
