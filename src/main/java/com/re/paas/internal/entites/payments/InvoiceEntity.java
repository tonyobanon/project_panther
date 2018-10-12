package com.re.paas.internal.entites.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfTrue;
import com.re.paas.api.classes.ClientRBRef;

@Cache
@Entity
public class InvoiceEntity {

	@Id
	Long id;
	
	@Index
	String accountId;
	
	@Index(IfTrue.class)
	boolean isOutstanding;
	
	//@Index
	Integer status;
	
	Date startDate;
	
	Date endDate;
	
	List<Long> items;
	
	String currency;
	
	BigDecimal totalDue;
	
	ClientRBRef comment;
	
	Date dateCreated;

	Date dateUpdated;
	
	public InvoiceEntity() {
		this.items = new ArrayList<Long>();
	}
	
	public Long getId() {
		return id;
	}

	public InvoiceEntity setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Long getAccountId() {
		return Long.parseLong(accountId);
	}

	public InvoiceEntity setAccountId(Long accountId) {
		this.accountId = accountId.toString();
		return this;
	}

	public boolean isOutstanding() {
		return isOutstanding;
	}

	public InvoiceEntity setOutstanding(boolean isOutstanding) {
		this.isOutstanding = isOutstanding;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public InvoiceEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public InvoiceEntity setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public InvoiceEntity setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public List<Long> getItems() {
		return items;
	}

	public InvoiceEntity setItems(List<Long> items) {
		this.items = items;
		return this;
	}
	
	public InvoiceEntity addItem(Long item) {
		this.items.add(item);
		return this;
	}
	
	public InvoiceEntity removeItem(Long item) {
		this.items.remove(item);
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public InvoiceEntity setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public BigDecimal getTotalDue() {
		return totalDue;
	}

	public InvoiceEntity setTotalDue(BigDecimal totalDue) {
		this.totalDue = totalDue;
		return this;
	}
	
	public InvoiceEntity incrementTotalDue(BigDecimal amount) {
		this.totalDue = totalDue.add(amount);
		return this;
	}
	
	public InvoiceEntity decrementTotalDue(BigDecimal amount) {
		this.totalDue = totalDue.subtract(amount);
		return this;
	}

	public ClientRBRef getComment() {
		return comment;
	}

	public InvoiceEntity setComment(ClientRBRef comment) {
		this.comment = comment;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public InvoiceEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public InvoiceEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
