package com.re.paas.internal.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.utils.Dates;

public class InvoiceSpec {

	Long id;

	String accountId;

	InvoiceStatus status;

	Date startDate;
	
	Date endDate;
	
	List<InvoiceItem> items;

	String currency;

	BigDecimal totalDue;

	ClientRBRef comment;

	Date dateCreated;

	Date dateUpdated;

	public InvoiceSpec() {
		this.items = new ArrayList<InvoiceItem>();
	}

	public static InvoiceSpec create(Long acountId, String currency, ClientRBRef comment) {
		return new InvoiceSpec().setAccountId(acountId).setStatus(InvoiceStatus.CREATED)
				.setItems(new ArrayList<>()).setCurrency(currency).setTotalDue(BigDecimal.ZERO).setComment(comment)
				.setDateCreated(Dates.now()).setDateUpdated(Dates.now());
	}

	public Long getId() {
		return id;
	}

	public InvoiceSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Long getAccountId() {
		return Long.parseLong(accountId);
	}

	public InvoiceSpec setAccountId(Long accountId) {
		this.accountId = accountId.toString();
		return this;
	}
	
	public InvoiceStatus getStatus() {
		return status;
	}

	public InvoiceSpec setStatus(InvoiceStatus status) {
		this.status = status;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public InvoiceSpec setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public InvoiceSpec setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public List<InvoiceItem> getItems() {
		return items;
	}

	public InvoiceSpec setItems(List<InvoiceItem> items) {
		this.items = items;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public InvoiceSpec setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public BigDecimal getTotalDue() {
		return totalDue;
	}

	public InvoiceSpec setTotalDue(BigDecimal totalDue) {
		this.totalDue = totalDue;
		return this;
	}

	public ClientRBRef getComment() {
		return comment;
	}

	public InvoiceSpec setComment(ClientRBRef comment) {
		this.comment = comment;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public InvoiceSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public InvoiceSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
