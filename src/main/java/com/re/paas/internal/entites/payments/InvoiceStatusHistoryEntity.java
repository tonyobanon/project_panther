package com.re.paas.internal.entites.payments;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.re.paas.api.classes.ClientRBRef;

@Cache
@Entity
public class InvoiceStatusHistoryEntity {
	
	@Id
	Long id;
	
	@Index
	String invoiceId;
	
	Integer status;
	
	ClientRBRef message;
	
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public InvoiceStatusHistoryEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getInvoiceId() {
		return Long.parseLong(invoiceId);
	}

	public InvoiceStatusHistoryEntity setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId.toString();
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public InvoiceStatusHistoryEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public ClientRBRef getMessage() {
		return message;
	}

	public InvoiceStatusHistoryEntity setMessage(ClientRBRef message) {
		this.message = message;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public InvoiceStatusHistoryEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
