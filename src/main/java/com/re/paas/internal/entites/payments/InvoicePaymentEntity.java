package com.re.paas.internal.entites.payments;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.re.paas.api.classes.ClientRBRef;

@Cache
@Entity
public class InvoicePaymentEntity {

	@Id
	Long invoiceId;

	String merchantReference;
	
	@Index
	String extReference;

	Integer status;

	ClientRBRef message;

	Date dateCreated;

	Date dateUpdated;

	public Long getInvoiceId() {
		return invoiceId;
	}

	public InvoicePaymentEntity setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}

	public Long getMerchantReference() {
		return Long.parseLong(merchantReference);
	}

	public InvoicePaymentEntity setMerchantReference(Long merchantReference) {
		this.merchantReference = merchantReference.toString();
		return this;
	}

	public String getExtReference() {
		return extReference;
	}

	public InvoicePaymentEntity setExtReference(String extReference) {
		this.extReference = extReference;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public InvoicePaymentEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public ClientRBRef getMessage() {
		return message;
	}

	public InvoicePaymentEntity setMessage(ClientRBRef message) {
		this.message = message;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public InvoicePaymentEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public InvoicePaymentEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
