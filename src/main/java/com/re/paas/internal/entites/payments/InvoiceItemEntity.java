package com.re.paas.internal.entites.payments;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.re.paas.api.classes.ClientRBRef;

@Cache
@Entity
public class InvoiceItemEntity {

	@Id
	Long id;

	ClientRBRef name;
	
	ClientRBRef description;

	Double amount;
	
	Date dateCreated;

	public Long getId() {
		return id;
	}

	public InvoiceItemEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public ClientRBRef getName() {
		return name;
	}

	public InvoiceItemEntity setName(ClientRBRef name) {
		this.name = name;
		return this;
	}

	public ClientRBRef getDescription() {
		return description;
	}

	public InvoiceItemEntity setDescription(ClientRBRef description) {
		this.description = description;
		return this;
	}

	public Double getAmount() {
		return amount;
	}

	public InvoiceItemEntity setAmount(Double amount) {
		this.amount = amount;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public InvoiceItemEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

}
