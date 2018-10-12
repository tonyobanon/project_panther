package com.re.paas.internal.entites.payments;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.re.paas.api.classes.ClientRBRef;

@Cache
@Entity
public class InvoicePaymentHistoryEntity {

	@Id
	Long id;

	@Index
	String invoiceId;

	@Index
	String extReference;

	Integer status;

	ClientRBRef message;

	String additionalInfo;
	
	Integer previousStatus;
	
	Boolean isOverwritten;
	
	Boolean isReconciled;

	Date dateCreated;

	Date dateUpdated;

	
	public Long getId() {
		return id;
	}

	public InvoicePaymentHistoryEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getInvoiceId() {
		return Long.parseLong(invoiceId);
	}

	public InvoicePaymentHistoryEntity setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId.toString();
		return this;
	}

	public String getExtReference() {
		return extReference;
	}

	public InvoicePaymentHistoryEntity setExtReference(String extReference) {
		this.extReference = extReference;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public InvoicePaymentHistoryEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public ClientRBRef getMessage() {
		return message;
	}

	public InvoicePaymentHistoryEntity setMessage(ClientRBRef message) {
		this.message = message;
		return this;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public InvoicePaymentHistoryEntity setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}

	public Integer getPreviousStatus() {
		return previousStatus;
	}

	public InvoicePaymentHistoryEntity setPreviousStatus(Integer previousStatus) {
		this.previousStatus = previousStatus;
		return this;
	}

	public Boolean getIsOverwritten() {
		return isOverwritten;
	}

	public InvoicePaymentHistoryEntity setIsOverwritten(Boolean isOverwritten) {
		this.isOverwritten = isOverwritten;
		return this;
	}

	public boolean isReconciled() {
		return isReconciled;
	}

	public InvoicePaymentHistoryEntity setReconciled(boolean isReconciled) {
		this.isReconciled = isReconciled;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public InvoicePaymentHistoryEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public InvoicePaymentHistoryEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
