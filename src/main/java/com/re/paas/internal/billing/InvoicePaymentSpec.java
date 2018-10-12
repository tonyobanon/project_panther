package com.re.paas.internal.billing;

import java.util.Date;

import com.re.paas.api.classes.ClientRBRef;

public class InvoicePaymentSpec {

	Long invoiceId;

	Long merchantReference;
	
	String extReference;

	InvoicePaymentStatus status;

	ClientRBRef message;

	String additionalInfo;
	
	Integer previousStatus;
	
	boolean isOverwritten;
	
	boolean isReconciled;
	
	Date dateCreated;

	Date dateUpdated;

	public Long getInvoiceId() {
		return invoiceId;
	}

	public InvoicePaymentSpec setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}

	public Long getMerchantReference() {
		return merchantReference;
	}

	public InvoicePaymentSpec setMerchantReference(Long merchantReference) {
		this.merchantReference = merchantReference;
		return this;
	}

	public String getExtReference() {
		return extReference;
	}

	public InvoicePaymentSpec setExtReference(String extReference) {
		this.extReference = extReference;
		return this;
	}

	public InvoicePaymentStatus getStatus() {
		return status;
	}

	public InvoicePaymentSpec setStatus(InvoicePaymentStatus status) {
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

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public InvoicePaymentSpec setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}

	public Integer getPreviousStatus() {
		return previousStatus;
	}

	public InvoicePaymentSpec setPreviousStatus(Integer previousStatus) {
		this.previousStatus = previousStatus;
		return this;
	}

	public boolean isOverwritten() {
		return isOverwritten;
	}

	public InvoicePaymentSpec setOverwritten(boolean isOverwritten) {
		this.isOverwritten = isOverwritten;
		return this;
	}

	public boolean isReconciled() {
		return isReconciled;
	}

	public InvoicePaymentSpec setReconciled(boolean isReconciled) {
		this.isReconciled = isReconciled;
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
