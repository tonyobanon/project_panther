package com.re.paas.internal.billing;

import java.util.Date;

import com.re.paas.api.classes.ClientRBRef;

public class InvoiceItem {

	Long id;

	ClientRBRef name;
	
	ClientRBRef description;

	Double amount;
	
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public InvoiceItem setId(Long id) {
		this.id = id;
		return this;
	}

	public ClientRBRef getName() {
		return name;
	}

	public InvoiceItem setName(ClientRBRef name) {
		this.name = name;
		return this;
	}

	public ClientRBRef getDescription() {
		return description;
	}

	public InvoiceItem setDescription(ClientRBRef description) {
		this.description = description;
		return this;
	}

	public Double getAmount() {
		return amount;
	}

	public InvoiceItem setAmount(Double amount) {
		this.amount = amount;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public InvoiceItem setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

}
